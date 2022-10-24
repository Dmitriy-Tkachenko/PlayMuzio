package com.tk4dmitriy.playmuzio.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.tk4dmitriy.playmuzio.data.model.endpoints.currentUsersProfile.CurrentUsersProfile
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.FeaturedPlaylists
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.NewReleases
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.TrackRecommendations
import com.tk4dmitriy.playmuzio.data.model.endpoints.userTopTracks.TopTracks
import com.tk4dmitriy.playmuzio.data.repository.MainRepository
import com.tk4dmitriy.playmuzio.utils.Resource
import com.tk4dmitriy.playmuzio.utils.TAG
import retrofit2.Response

private const val TIME_RANGE_LONG_TERM = "long_term" // data for several a years
private const val TIME_RANGE_MEDIUM_TERM = "medium_term" // data for half a years
private const val TIME_RANGE_SHORT_TERM = "short_term" // data for last a month
private const val LIMIT = 5

class HomeViewModel(private val mainRepository: MainRepository): ViewModel() {
    private lateinit var currentUsersProfileResp: Response<CurrentUsersProfile>
    private lateinit var userTopTracks: Response<TopTracks>
    private lateinit var featuredPlaylistsResp: Response<FeaturedPlaylists>
    private lateinit var newReleasesResp: Response<NewReleases>
    private lateinit var trackRecommendations: Response<TrackRecommendations>

    fun fetchFeaturedPlaylists() = Transformations.switchMap(fetchCurrentUsersProfile()) {
        it.body()?.run {
            Transformations.switchMap(liveFeaturedPlaylists(country)) { resultFeaturedPlaylists ->
                resultFeaturedPlaylists.data?.body()?.playlists?.items?.forEach { item ->
                    item.images?.forEach { image ->
                        if (image.height == 300 || image.height == null) item.imageUrl = image.url
                    }
                }
                MutableLiveData(resultFeaturedPlaylists)
            }
        }
    }

    fun fetchNewReleases() = Transformations.switchMap(fetchCurrentUsersProfile()) {
        it.body()?.run {
            Transformations.switchMap(liveNewReleases(country)) { resultNewReleases ->
                resultNewReleases.data?.body()?.albums?.items?.forEach { item ->
                    val names: MutableList<String> = mutableListOf()
                    item.artists?.forEach { artist ->
                        names.add(artist.name)
                    }
                    val artistsNames = getArtists(names = names)
                    item.artistsNames = artistsNames

                    item.images?.forEach { image ->
                        if (image.height == 300 || image.height == null) item.imageUrl = image.url
                    }
                }

                MutableLiveData(resultNewReleases)
            }
        }
    }

    fun fetchRecommendations() = Transformations.switchMap(fetchUserTopTracks(timeRange = TIME_RANGE_MEDIUM_TERM)) {
        it.body()?.run {
            if (items.isNotEmpty()) {
                var seedTracks = ""
                items.forEach { topTrack ->
                    seedTracks += "${topTrack.id},"
                }
                Transformations.switchMap(liveRecommendationsByTracks(seedTracks = seedTracks)) { resultRecommendation ->
                    resultRecommendation.data?.body()?.let { trackRecommendations ->
                        trackRecommendations.trackNames.clear()
                        trackRecommendations.trackPreviewUrls.clear()
                        trackRecommendations.trackArtistNames.clear()
                        trackRecommendations.trackImageUrls.clear()
                        trackRecommendations.tracks?.forEach { track ->
                            trackRecommendations.trackNames.add(track.name)
                            trackRecommendations.trackPreviewUrls.add(track.previewUrl)

                            val names: MutableList<String> = mutableListOf()
                            track.artists?.forEach { artist ->
                                names.add(artist.name)
                            }
                            track.artistsNames = getArtists(names = names)
                            trackRecommendations.trackArtistNames.add(track.artistsNames)

                            track.album?.run {
                                images?.forEach { image ->
                                    if (image.height == 300 || image.height == null) {
                                        trackRecommendations.trackImageUrls.add(image.url)
                                        imageUrl = image.url
                                    }
                                }
                            }
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

    private fun fetchUserTopTracks(timeRange: String) = liveData {
        if (!::userTopTracks.isInitialized) {
            userTopTracks = mainRepository.fetchUserTopTracks(limit = LIMIT, timeRange = timeRange)
        }
        emit(userTopTracks)
    }

    private fun fetchRecommendationsLongTerm() = Transformations.switchMap(fetchUserTopTracks(timeRange = TIME_RANGE_LONG_TERM)) {
        it.body()?.run {
            if (items.isNotEmpty()) {
                var seedTracks = ""
                items.forEach { topTrack ->
                    seedTracks += "${topTrack.id},"
                }
                Transformations.switchMap(liveRecommendationsByTracks(seedTracks = seedTracks)) { resultRecommendation ->
                    resultRecommendation.data?.body()?.let { trackRecommendations ->
                        trackRecommendations.trackNames.clear()
                        trackRecommendations.trackPreviewUrls.clear()
                        trackRecommendations.trackArtistNames.clear()
                        trackRecommendations.trackImageUrls.clear()
                        trackRecommendations.tracks?.forEach { track ->
                            trackRecommendations.trackNames.add(track.name)
                            trackRecommendations.trackPreviewUrls.add(track.previewUrl)

                            val names: MutableList<String> = mutableListOf()
                            track.artists?.forEach { artist ->
                                names.add(artist.name)
                            }
                            track.artistsNames = getArtists(names = names)
                            trackRecommendations.trackArtistNames.add(track.artistsNames)

                            track.album?.run {
                                images?.forEach { image ->
                                    if (image.height == 300 || image.height == null) {
                                        trackRecommendations.trackImageUrls.add(image.url)
                                        imageUrl = image.url
                                    }
                                }
                            }
                        }
                        MutableLiveData(resultRecommendation)
                    }
                }
            } else {
                // Добавить возможность выбора любимых жанров
                Transformations.switchMap(liveRecommendationsByGenres(seedGenres = "hip-hop")) { resultRecommendation ->
                    resultRecommendation.data?.body()?.let { trackRecommendations ->
                        trackRecommendations.trackNames.clear()
                        trackRecommendations.trackPreviewUrls.clear()
                        trackRecommendations.trackArtistNames.clear()
                        trackRecommendations.trackImageUrls.clear()
                        trackRecommendations.tracks?.forEach { track ->
                            trackRecommendations.trackNames.add(track.name)
                            trackRecommendations.trackPreviewUrls.add(track.previewUrl)

                            val names: MutableList<String> = mutableListOf()
                            track.artists?.forEach { artist ->
                                names.add(artist.name)
                            }
                            track.artistsNames = getArtists(names = names)
                            trackRecommendations.trackArtistNames.add(track.artistsNames)

                            track.album?.run {
                                images?.forEach { image ->
                                    if (image.height == 300 || image.height == null) {
                                        trackRecommendations.trackImageUrls.add(image.url)
                                        imageUrl = image.url
                                    }
                                }
                            }
                        }
                        MutableLiveData(resultRecommendation)
                    }
                }
            }
        }
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
}