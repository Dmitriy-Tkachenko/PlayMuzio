package com.tk4dmitriy.playmuzio.data.model.endpoints.playlist

import com.squareup.moshi.Json
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

data class Track(
    @Json(name = "album") val album: Album? = null,
    @Json(name = "artists") val artists: List<Artist>? = null,
    @Json(name = "duration_ms") val durationMs: Long? = null,
    @Json(name = "name") @NullToEmptyString val name: String = "",
    @Json(name = "href") @NullToEmptyString val href: String = "",
    @Json(name = "preview_url") @NullToEmptyString val previewUrl: String = "",
    var artistNames: String = "",
    var durationMin: String = ""
)
