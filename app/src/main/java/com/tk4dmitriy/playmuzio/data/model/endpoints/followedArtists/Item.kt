package com.tk4dmitriy.playmuzio.data.model.endpoints.followedArtists

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "id") val id: String
)
