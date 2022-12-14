package com.tk4dmitriy.playmuzio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tk4dmitriy.playmuzio.data.api.ApiHelper
import com.tk4dmitriy.playmuzio.data.repository.MainRepository

class ViewModelFactory(apiHelper: ApiHelper): ViewModelProvider.Factory {
    private val mainRepository: MainRepository by lazy(LazyThreadSafetyMode.NONE) {
        MainRepository(apiHelper = apiHelper)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(mainRepository = mainRepository) as T
        } else if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            return AlbumViewModel(mainRepository = mainRepository) as T
        } else if (modelClass.isAssignableFrom(TrackViewModel::class.java)) {
            return TrackViewModel(mainRepository = mainRepository) as T
        } else if (modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
            return PlaylistViewModel(mainRepository = mainRepository) as T
        } else {
            throw IllegalArgumentException("Unknown class name")
        }
    }
}