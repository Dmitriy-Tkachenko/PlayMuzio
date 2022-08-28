package com.tk4dmitriy.playmuzio.data.model.endpoints.album

import com.google.gson.annotations.SerializedName

data class Album(
    val artists: List<Artist> = emptyList(),
    val images: List<Image> = emptyList(),
    val name: String = "",
    @SerializedName("release_date") val releaseDate: String = "",
    @SerializedName("total_tracks") val totalTracks: Int = 0,
    val tracks: Tracks,

    var artistNames: String = ""
)