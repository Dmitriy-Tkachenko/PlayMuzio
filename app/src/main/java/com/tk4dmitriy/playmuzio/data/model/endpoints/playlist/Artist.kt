package com.tk4dmitriy.playmuzio.data.model.endpoints.playlist

import com.squareup.moshi.Json
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

data class Artist(
    @Json(name = "name") @NullToEmptyString val name: String = ""
)
