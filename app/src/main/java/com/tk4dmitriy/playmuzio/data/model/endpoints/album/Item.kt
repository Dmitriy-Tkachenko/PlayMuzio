package com.tk4dmitriy.playmuzio.data.model.endpoints.album

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackFromAlbum.Image
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "artists") val artists: List<Artist>? = null,
    @Json(name = "name") @NullToEmptyString val name: String = "",
    @Json(name = "href") @NullToEmptyString val href: String = "",
    @Json(name = "preview_url") @NullToEmptyString val previewUrl: String = "",
    @Json(name = "duration_ms") val durationMs: Long? = null,

    var artistsNames: String = "",
    var durationMin: String = ""
)
