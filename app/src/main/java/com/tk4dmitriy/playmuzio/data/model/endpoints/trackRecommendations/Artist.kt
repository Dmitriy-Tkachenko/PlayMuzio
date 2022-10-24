package com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Artist(
    @Json(name = "name") @NullToEmptyString val name: String = ""
)