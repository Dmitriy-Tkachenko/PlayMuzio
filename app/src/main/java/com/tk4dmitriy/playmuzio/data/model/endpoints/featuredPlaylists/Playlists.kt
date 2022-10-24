package com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Playlists(
    @Json(name = "items") val items: List<Item>? = null
)
