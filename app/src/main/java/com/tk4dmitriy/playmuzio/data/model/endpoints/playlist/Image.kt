package com.tk4dmitriy.playmuzio.data.model.endpoints.playlist

import com.squareup.moshi.Json
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

data class Image(
    @Json(name = "height") val height: Int? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "url") @NullToEmptyString val url: String = ""
)
