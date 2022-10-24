package com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tk4dmitriy.playmuzio.utils.NullToEmptyString

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "description") @NullToEmptyString val description: String = "",
    @Json(name = "href") @NullToEmptyString val href: String = "",
    @Json(name = "images") val images: List<Image>? = null,
    @Json(name = "name") @NullToEmptyString val name: String = "",

    var imageUrl: String = ""
)
