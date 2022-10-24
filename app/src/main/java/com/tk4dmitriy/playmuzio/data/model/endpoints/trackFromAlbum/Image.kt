package com.tk4dmitriy.playmuzio.data.model.endpoints.trackFromAlbum

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "height") val height: Int = 0,
    @Json(name = "width") val width: Int = 0,
    @Json(name = "url") val url: String = ""
)