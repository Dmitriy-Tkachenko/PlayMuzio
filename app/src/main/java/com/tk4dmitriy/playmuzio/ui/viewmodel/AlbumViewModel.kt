package com.tk4dmitriy.playmuzio.ui.viewmodel

import androidx.lifecycle.*
import com.tk4dmitriy.playmuzio.data.model.endpoints.album.Album
import com.tk4dmitriy.playmuzio.data.model.endpoints.album.Item
import com.tk4dmitriy.playmuzio.data.repository.MainRepository
import com.tk4dmitriy.playmuzio.utils.Resource
import retrofit2.Response
import java.util.concurrent.TimeUnit

class AlbumViewModel(private val mainRepository: MainRepository): ViewModel() {
    private lateinit var albumResp: Response<Album>

    fun fetchAlbum(url: String) = Transformations.switchMap(liveAlbum(url = url)) {
        it.data?.body()?.run {
            val albumArtistNames: MutableList<String> = mutableListOf()
            artists.forEach { artist -> albumArtistNames.add(artist.name) }
            artistNames = getArtistsNames(names = albumArtistNames)

            tracks.items.forEach { item ->
                val trackArtistsNames: MutableList<String> = mutableListOf()
                item.artists.forEach { artist ->
                    trackArtistsNames.add(artist.name)
                }
                item.artistsNames = getArtistsNames(names = trackArtistsNames)
                item.durationMin = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(item.durationMs),
                    TimeUnit.MILLISECONDS.toSeconds(item.durationMs) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(item.durationMs)))
            }
            tracks.info = getInfo(items = tracks.items, date = releaseDate, totalTracks = totalTracks)

            MutableLiveData(it)
        }
    }

    private fun liveAlbum(url: String) = liveData {
        if (!::albumResp.isInitialized) {
            emit(Resource.Loading())
            try {
                val data = mainRepository.fetchAlbum(url = url)
                albumResp = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = albumResp))
    }

    private fun getArtistsNames(names: List<String>): String {
        var result = ""

        for (index in names.indices) {
            if (names.size > 1 && index != names.size - 1) {
                result += "${names[index]}, "
            }
        }
        result += names[names.size - 1]

        return result
    }

    private fun getInfo(items: List<Item>, date: String, totalTracks: Int): String {
        val minutes = getMinutesAlbum(items)
        return "$date | $totalTracks songs | $minutes min"
    }

    private fun getMinutesAlbum(items: List<Item>): Long {
        var duration: Long = 0

        items.forEach { item -> duration += item.durationMs }

        duration = TimeUnit.MILLISECONDS.toMinutes(duration)

        return duration
    }
}