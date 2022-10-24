package com.tk4dmitriy.playmuzio.data.model.endpoints.matcherLyrics

import com.squareup.moshi.Json

data class MatcherLyrics(
    @Json(name = "message") val message: Message? = null
)
