package com.tk4dmitriy.playmuzio.data.model.endpoints.currentUsersProfile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentUsersProfile(
    @Json(name = "country") val country: String = "US"
)
