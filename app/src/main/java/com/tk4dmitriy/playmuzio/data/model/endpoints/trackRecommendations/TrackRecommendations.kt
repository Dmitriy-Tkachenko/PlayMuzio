package com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackRecommendations(
    @Json(name = "tracks") val tracks: List<Track>? = null,
    var trackPreviewUrls: MutableList<String> = mutableListOf(),
    var trackNames: MutableList<String> = mutableListOf(),
    var trackArtistNames: MutableList<String> = mutableListOf(),
    var trackImageUrls: MutableList<String> = mutableListOf()
)