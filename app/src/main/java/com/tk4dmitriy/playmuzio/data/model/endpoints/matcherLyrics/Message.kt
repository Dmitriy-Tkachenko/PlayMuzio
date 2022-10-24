package com.tk4dmitriy.playmuzio.data.model.endpoints.matcherLyrics

import com.squareup.moshi.Json

data class Message(
    @Json(name = "body") val body: Body? = null
)
