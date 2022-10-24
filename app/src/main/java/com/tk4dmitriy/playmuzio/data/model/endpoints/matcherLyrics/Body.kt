package com.tk4dmitriy.playmuzio.data.model.endpoints.matcherLyrics

import com.squareup.moshi.Json

data class Body(
    @Json(name = "lyrics") val lyrics: Lyrics? = null
)
