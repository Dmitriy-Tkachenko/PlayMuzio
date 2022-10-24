package com.tk4dmitriy.playmuzio.data.model.endpoints.album

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Album(
    @Json(name = "release_date") @NullToEmptyString val releaseDate: String = "",
    @Json(name = "total_tracks") val totalTracks: Int? = null,
    @Json(name = "tracks") val tracks: Tracks? = null
)