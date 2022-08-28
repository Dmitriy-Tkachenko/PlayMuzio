package com.tk4dmitriy.playmuzio.data.model.endpoints.album

import com.google.gson.annotations.SerializedName

data class Item(
    val artists: List<Artist> = emptyList(),
    val name: String = "",
    val href: String = "",
    @SerializedName("duration_ms") val durationMs: Long = 0,
    var artistsNames: String = "",
    var durationMin: String = ""
)
