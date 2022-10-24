package com.tk4dmitriy.playmuzio.data.model.endpoints.playlist

import com.squareup.moshi.Json

data class Album(
    @Json(name = "images") val images: List<Image>? = null,
    var imageUrl: String = ""
)
