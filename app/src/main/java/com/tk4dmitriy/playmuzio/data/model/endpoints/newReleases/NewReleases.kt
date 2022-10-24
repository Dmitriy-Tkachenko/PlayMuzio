package com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewReleases(
    @Json(name = "albums") val albums: Albums? = null
)
