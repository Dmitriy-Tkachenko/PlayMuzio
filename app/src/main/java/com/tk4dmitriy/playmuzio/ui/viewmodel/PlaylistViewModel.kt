package com.tk4dmitriy.playmuzio.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tk4dmitriy.playmuzio.data.model.endpoints.playlist.Playlist
import com.tk4dmitriy.playmuzio.data.model.endpoints.playlist.Item
import com.tk4dmitriy.playmuzio.data.repository.MainRepository
import com.tk4dmitriy.playmuzio.utils.Resource
import retrofit2.Response
import java.util.concurrent.TimeUnit

class PlaylistViewModel(private val mainRepository: MainRepository): ViewModel() {
    private lateinit var albumResp: Response<Playlist>

    fun fetchPlaylist(url: String) = Transformations.switchMap(livePlaylist(url = url)) { playlistResp ->
        playlistResp.data?.body()?.tracks?.let { tracks ->
            tracks.trackNames.clear()
            tracks.trackArtistNames.clear()
            tracks.trackPreviewUrls.clear()
            tracks.trackImageUrls.clear()
            tracks.items?.forEach { item ->
                item.track?.let { track ->
                    tracks.trackPreviewUrls.add(track.previewUrl)
                    tracks.trackNames.add(track.name)
                    val totalTracks = if (tracks.totalTracks!! > 100) 100 else tracks.totalTracks
                    tracks.info = getInfo(items = tracks.items, totalTracks = totalTracks)

                    val artistNames: MutableList<String> = mutableListOf()
                    track.artists?.forEach { artist ->
                        artistNames.add(artist.name)
                    }
                    track.artistNames = getArtistNames(names = artistNames)
                    tracks.trackArtistNames.add(track.artistNames)

                    track.album?.images?.forEach { image ->
                        if (image.height == 300 || image.height == null) {
                            tracks.trackImageUrls.add(image.url)
                            track.album.imageUrl = image.url
                        }
                    }

                    track.durationMs?.run {
                        track.durationMin = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(this),
                            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this)))
                    }
                }
            }

            MutableLiveData(playlistResp)
        }
    }

    private fun livePlaylist(url: String) = liveData {
        if (!::albumResp.isInitialized) {
            emit(Resource.Loading())
            try {
                val data = mainRepository.fetchPlaylist(url = url)
                albumResp = data
                emit(Resource.Success(data = data))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        } else emit(Resource.Success(data = albumResp))
    }

    private fun getArtistNames(names: List<String>): String {
        var result = ""

        for (index in names.indices) {
            if (names.size > 1 && index != names.size - 1) {
                result += "${names[index]}, "
            }
        }
        result += names[names.size - 1]

        return result
    }

    private fun getInfo(items: List<Item>, totalTracks: Int): String {
        val minutes = getMinutesAlbum(items)
        return "$totalTracks songs | $minutes min"
    }

    private fun getMinutesAlbum(items: List<Item>): Long {
        var duration: Long = 0
        items.forEach { item -> duration += item.track?.durationMs ?: 0 }
        duration = TimeUnit.MILLISECONDS.toMinutes(duration)
        return duration
    }
}