package com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Album(
    @Json(name = "images") val images: List<Image>? = null,
    var imageUrl: String = ""
)
