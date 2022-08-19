package com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases

data class Item(
    val name: String?,
    val href: String?,
    val artists: List<Artists>?,
    val images: List<Image>?
)
