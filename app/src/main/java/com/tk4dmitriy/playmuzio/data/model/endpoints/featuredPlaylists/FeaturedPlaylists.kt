package com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class FeaturedPlaylists(
    @Json(name = "message") @NullToEmptyString val message: String = "",
    @Json(name = "playlists") val playlists: Playlists? = null
)
