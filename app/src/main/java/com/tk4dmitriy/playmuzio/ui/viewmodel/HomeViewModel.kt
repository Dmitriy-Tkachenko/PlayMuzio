package com.tk4dmitriy.playmuzio.ui.viewmodel

import androidx.lifecycle.*
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.FeaturedPlaylists
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.NewReleases
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.TrackRecommendations
import com.tk4dmitriy.playmuzio.data.repository.MainRepository
import com.tk4dmitriy.playmuzio.utils.Constants
import com.tk4dmitriy.playmuzio.utils.Resource
import retrofit2.Response

class HomeViewModel(private val mainRepository: MainRepository): ViewModel() {
    private lateinit var featuredPlaylistsResp: Response<FeaturedPlaylists>
    private lateinit var newReleasesResp: Response<NewReleases>
    private lateinit var trackRecommendations: Response<TrackRecommendations>

    fun fetchFeaturedPlaylists() = Transformations.switchMap(liveCurrentUsersProfile()) {
        val country = it.body()?.country
        country?.run {
            liveFeaturedPlaylists(this)
        } ?: liveFeaturedPlaylists("US")
    }

    fun fetchNewReleases() = Transformations.switchMap(liveCurrentUsersProfile()) {
        val country = it.body()?.country
        country?.run {
            liveNewReleases(this)
        } ?: liveNewReleases("US")
    }

    fun fetchRecommendations() = Transformations.switchMap(liveUserTopTracks()) {
        val topTracks = it.body()?.items
        topTracks?.run {
            var seedTracks = ""
            topTracks.forEach { topTrack ->
                seedTracks += "${topTrack.id},"
            }
            liveRecommendations(seedTracks = seedTracks)
        }
    }


    private fun liveCurrentUsersProfile() = liveData {
        emit(mainRepository.fetchCurrentUsersProfile())
    }

    private fun liveUserTopTracks() = liveData {
        emit(mainRepository.fetchUserTopTracks(limit = 5))
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

    private fun liveRecommendations(seedTracks: String) = liveData {
        if (!::trackRecommendations.isInitialized) {
            emit(Resource.Loading())
            try {
                val data = mainRepository.fetchTrackRecommendations(seedTracks = seedTracks, timeRange = Constants.TIME_RANGE_MEDIUM_TERM)
                trackRecommendations = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = trackRecommendations))
    }
}