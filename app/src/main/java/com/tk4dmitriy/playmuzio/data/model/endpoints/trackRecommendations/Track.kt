package com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Track(
    @Json(name = "album") val album: Album? = null,
    @Json(name = "artists") val artists: List<Artist>? = null,
    @Json(name = "href") @NullToEmptyString val href: String = "",
    @Json(name = "name") @NullToEmptyString val name: String = "",
    @Json(name= "preview_url") @NullToEmptyString val previewUrl: String = "",

    var artistsNames: String = ""
)