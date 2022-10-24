package com.tk4dmitriy.playmuzio.data.model.endpoints.playlist

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackFromAlbum.Image
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "track") val track: Track? = null,
    @Json(name = "added_at") @NullToEmptyString val addedAt: String = ""
)
