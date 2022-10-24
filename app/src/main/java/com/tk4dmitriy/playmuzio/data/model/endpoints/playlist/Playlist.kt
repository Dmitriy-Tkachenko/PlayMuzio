package com.tk4dmitriy.playmuzio.data.model.endpoints.playlist

import com.squareup.moshi.Json
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

data class Playlist(
    @Json(name = "name") @NullToEmptyString val name: String = "",
    @Json(name = "description") @NullToEmptyString val description: String = "",
    @Json(name = "tracks") val tracks: Tracks? = null
)
