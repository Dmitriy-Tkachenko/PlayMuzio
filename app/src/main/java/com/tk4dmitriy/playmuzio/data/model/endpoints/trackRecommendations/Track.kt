package com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations

data class Track(
    val album: Album?,
    val artists: List<Artist> = emptyList(),
    val href: String = "",
    val name: String = "",
    var artistsNames: String = ""
)
