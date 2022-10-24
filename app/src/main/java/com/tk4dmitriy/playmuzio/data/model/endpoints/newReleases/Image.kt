package com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null,
    @Json(name = "url") @NullToEmptyString val url: String = "",
)