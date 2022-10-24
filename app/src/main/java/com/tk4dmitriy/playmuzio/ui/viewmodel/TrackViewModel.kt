package com.tk4dmitriy.playmuzio.ui.viewmodel

import androidx.lifecycle.*
import com.tk4dmitriy.playmuzio.data.model.endpoints.matcherLyrics.MatcherLyrics
import com.tk4dmitriy.playmuzio.data.repository.MainRepository
import com.tk4dmitriy.playmuzio.utils.Constants
import com.tk4dmitriy.playmuzio.utils.Resource
import retrofit2.Response

class TrackViewModel(private val mainRepository: MainRepository): ViewModel() {
    private lateinit var trackLyrics: Response<MatcherLyrics>
    private var trackName: String = ""
    private var artistName: String = ""

    fun fetchTrackLyrics(track: String, artist: String) = liveTrackLyrics(track = track, artist = artist)

    private fun liveTrackLyrics(track: String, artist: String) = liveData {
        if (trackName != track || artistName != artist) {
            emit(Resource.Loading())
            try {
                val fullUrl = Constants.API_URL_MUSIX_MATCH + Constants.TRACK_LYRICS
                val data = mainRepository.fetchTrackLyrics(url = fullUrl, apiKey = Constants.API_KEY, track = track, artist = artist)
                trackLyrics = data
                trackName = track
                artistName = artist
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = trackLyrics))
    }
}