package com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases

data class Item(
    val name: String = "",
    val href: String = "",
    val artists: List<Artist> = emptyList(),
    val images: List<Image> = emptyList(),
    var artistsNames: String = ""
)
