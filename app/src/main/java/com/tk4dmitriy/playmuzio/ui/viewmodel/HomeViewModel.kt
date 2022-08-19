package com.tk4dmitriy.playmuzio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tk4dmitriy.playmuzio.data.model.endpoints.currentUsersProfile.CurrentUsersProfile
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.FeaturedPlaylists
import com.tk4dmitriy.playmuzio.data.repository.MainRepository
import com.tk4dmitriy.playmuzio.utils.Resource
import retrofit2.Response

class HomeViewModel(private val mainRepository: MainRepository): ViewModel() {
    private lateinit var currentUsersProfileResp: Response<CurrentUsersProfile>
    private lateinit var featuredPlaylistsResp: Response<FeaturedPlaylists>

    fun fetchCurrentUsersProfile() = liveData {
        if (!::currentUsersProfileResp.isInitialized) {
            emit(Resource.Loading())
            try {
                val data = mainRepository.fetchCurrentUsersProfile()
                currentUsersProfileResp = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = currentUsersProfileResp))
    }

    fun fetchFeaturedPlaylists(country: String) = liveData {
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
}