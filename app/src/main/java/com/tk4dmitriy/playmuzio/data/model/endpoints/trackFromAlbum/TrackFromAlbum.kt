package com.tk4dmitriy.playmuzio.data.model.endpoints.trackFromAlbum

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackFromAlbum(
    @Json(name = "album") val album: Album
    /*@Json(name = "artists") val artists: List<Artist> = emptyList(),
    @Json(name = "name") val name: String = "",
    @Json(name = "preview_url") val preview_url: String = "",
    @Json(name = "uri") val uri: String = "",

    var artistsNames: String = "",*/
)