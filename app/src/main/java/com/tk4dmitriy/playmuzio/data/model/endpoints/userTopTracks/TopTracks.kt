package com.tk4dmitriy.playmuzio.data.model.endpoints.userTopTracks

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopTracks(
    @Json(name = "items") val items: List<Item> = emptyList()
)