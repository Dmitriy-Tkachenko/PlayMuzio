package com.tk4dmitriy.playmuzio.data.repository

import com.tk4dmitriy.playmuzio.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun fetchCurrentUsersProfile() = apiHelper.fetchCurrentUsersProfile()
    suspend fun fetchFeaturedPlaylists(country: String) = apiHelper.fetchFeaturedPlaylists(country = country)
}