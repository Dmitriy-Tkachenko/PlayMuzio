package com.tk4dmitriy.playmuzio.data.model.endpoints.matcherLyrics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Lyrics(
    @Json(name = "lyrics_body") @NullToEmptyString val lyricsBody: String = ""
)