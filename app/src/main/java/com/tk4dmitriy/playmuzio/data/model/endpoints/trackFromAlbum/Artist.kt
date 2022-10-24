package com.tk4dmitriy.playmuzio.data.model.endpoints.trackFromAlbum

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(
    @Json(name = "name") val name: String = ""
)