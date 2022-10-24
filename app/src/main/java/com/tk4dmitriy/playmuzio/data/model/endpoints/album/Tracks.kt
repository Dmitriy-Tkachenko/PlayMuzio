package com.tk4dmitriy.playmuzio.data.model.endpoints.album

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.data.model.endpoints.playlist.Track
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Tracks(
    @Json(name = "items") val items: List<Item>? = null,

    var info: String = "",
    var trackPreviewUrls: MutableList<String> = mutableListOf(),
    var trackNames: MutableList<String> = mutableListOf(),
    var trackArtistNames: MutableList<String> = mutableListOf(),
)