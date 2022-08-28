package com.tk4dmitriy.playmuzio.data.model.endpoints.album

data class Tracks(
    val items: List<Item> = emptyList(),
    var info: String = ""
)
