package com.tk4dmitriy.playmuzio.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.tk4dmitriy.playmuzio.data.model.endpoints.browseCategories.BrowseCategories
import com.tk4dmitriy.playmuzio.data.model.endpoints.currentUsersProfile.CurrentUsersProfile
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.FeaturedPlaylists
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.NewReleases
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.TrackRecommendations
import com.tk4dmitriy.playmuzio.data.model.endpoints.userTopTracks.TopTracks
import com.tk4dmitriy.playmuzio.data.repository.MainRepository
import com.tk4dmitriy.playmuzio.utils.Constants
import com.tk4dmitriy.playmuzio.utils.Resource
import com.tk4dmitriy.playmuzio.utils.TAG
import retrofit2.Response

class HomeViewModel(private val mainRepository: MainRepository): ViewModel() {
    private lateinit var currentUsersProfileResp: Response<CurrentUsersProfile>
    private lateinit var userTopTracks: Response<TopTracks>
    private lateinit var featuredPlaylistsResp: Response<FeaturedPlaylists>
    private lateinit var newReleasesResp: Response<NewReleases>
    private lateinit var trackRecommendations: Response<TrackRecommendations>
    private lateinit var browseCategories: Response<BrowseCategories>

    fun fetchFeaturedPlaylists() = Transformations.switchMap(fetchCurrentUsersProfile()) {
        it.body()?.run {
            liveFeaturedPlaylists(country)
        }
    }

    fun fetchBrowseCategories() = Transformations.switchMap(fetchCurrentUsersProfile()) {
        it.body()?.run {
            liveBrowseCategories(country)
        }
    }

    fun fetchNewReleases() = Transformations.switchMap(fetchCurrentUsersProfile()) {
        it.body()?.run {
            Transformations.switchMap(liveNewReleases(country)) { resultNewReleases ->
                resultNewReleases.data?.body()?.albums?.run {
                    items.forEach { item ->
                        val names: MutableList<String> = mutableListOf()
                        item.artists.forEach { artist ->
                            names.add(artist.name)
                        }
                        val artistsNames = getArtists(names = names)
                        item.artistsNames = artistsNames
                    }
                }
                MutableLiveData(resultNewReleases)
            }
        }
    }

    fun fetchRecommendations() = Transformations.switchMap(fetchUserTopTracks(limit = Constants.LIMIT, timeRange = Constants.TIME_RANGE_MEDIUM_TERM)) {
        it.body()?.run {
            if (items.isNotEmpty()) {
                var seedTracks = ""
                items.forEach { topTrack ->
                    seedTracks += "${topTrack.id},"
                }
                Transformations.switchMap(liveRecommendationsByTracks(seedTracks = seedTracks)) { resultRecommendation ->
                    resultRecommendation.data?.body()?.run {
                        tracks.forEach { track ->
                            val names: MutableList<String> = mutableListOf()
                            track.artists.forEach { artist ->
                                names.add(artist.name)
                            }
                            val artistsNames = getArtists(names = names)
                            track.artistsNames = artistsNames
                        }
                    }
                    MutableLiveData(resultRecommendation)
                }
            } else fetchRecommendationsLongTerm()
        }
    }

    private fun fetchCurrentUsersProfile() = liveData {
        if (!::currentUsersProfileResp.isInitialized) {
            currentUsersProfileResp = mainRepository.fetchCurrentUsersProfile()
        }
        emit(currentUsersProfileResp)
    }

    private fun fetchUserTopTracks(limit: Int, timeRange: String) = liveData {
        if (!::userTopTracks.isInitialized) {
            userTopTracks = mainRepository.fetchUserTopTracks(limit = limit, timeRange = timeRange)
        }
        emit(userTopTracks)
    }

    private fun fetchRecommendationsLongTerm() = Transformations.switchMap(fetchUserTopTracks(limit = Constants.LIMIT, timeRange = Constants.TIME_RANGE_LONG_TERM)) {
        it.body()?.run {
            if (items.isNotEmpty()) {
                var seedTracks = ""
                items.forEach { topTrack ->
                    seedTracks += "${topTrack.id},"
                }
                Transformations.switchMap(liveRecommendationsByTracks(seedTracks = seedTracks)) { resultRecommendation ->
                    resultRecommendation.data?.body()?.run {
                        tracks.forEach { track ->
                            val names: MutableList<String> = mutableListOf()
                            track.artists.forEach { artist ->
                                names.add(artist.name)
                            }
                            val artistsNames = getArtists(names = names)
                            track.artistsNames = artistsNames
                        }
                    }
                    MutableLiveData(resultRecommendation)
                }
            } else {
                // Добавить возможность выбора любимых жанров
                Transformations.switchMap(liveRecommendationsByGenres(seedGenres = "hip-hop")) { resultRecommendation ->
                    resultRecommendation.data?.body()?.run {
                        tracks.forEach { track ->
                            val names: MutableList<String> = mutableListOf()
                            track.artists.forEach { artist ->
                                names.add(artist.name)
                            }
                            val artistsNames = getArtists(names = names)
                            track.artistsNames = artistsNames
                        }
                    }
                    MutableLiveData(resultRecommendation)
                }
            }
        }
    }

    private fun getArtists(names: List<String>): String {
        var result = ""

        for (index in names.indices) {
            if (names.size > 1 && index != names.size - 1) {
                result += "${names[index]}, "
            }
        }
        result += names[names.size - 1]

        return result
    }

    private fun liveFeaturedPlaylists(country: String) = liveData {
        if (!::featuredPlaylistsResp.isInitialized) {
            emit(Resource.Loading())
            try {
                val data = mainRepository.fetchFeaturedPlaylists(country = country)
                featuredPlaylistsResp = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = featuredPlaylistsResp))
    }

    private fun liveNewReleases(country: String) = liveData {
        if (!::newReleasesResp.isInitialized) {
            emit(Resource.Loading())
            try {
                val data = mainRepository.fetchNewReleases(country = country)
                newReleasesResp = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = newReleasesResp))
    }

    private fun liveRecommendationsByTracks(seedTracks: String) = liveData {
        if (!::trackRecommendations.isInitialized) {
            emit(Resource.Loading())
            try {
                val data = mainRepository.fetchTrackRecommendationsByTracks(seedTracks = seedTracks)
                trackRecommendations = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = trackRecommendations))
    }

    private fun liveRecommendationsByGenres(seedGenres: String) = liveData {
        if (!::trackRecommendations.isInitialized) {
            emit(Resource.Loading())
            Log.d(TAG, "3")
            try {
                val data = mainRepository.fetchTrackRecommendationsByGenres(seedGenres = seedGenres)
                trackRecommendations = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = trackRecommendations))
    }

    private fun liveBrowseCategories(country: String) = liveData {
        if (!::browseCategories.isInitialized) {
            emit(Resource.Loading())
            try {
                val data = mainRepository.fetchBrowseCategories(country = country)
                browseCategories = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = browseCategories))
    }
}