package com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Albums(
    @Json(name = "items") val items: List<Item>? = null
)