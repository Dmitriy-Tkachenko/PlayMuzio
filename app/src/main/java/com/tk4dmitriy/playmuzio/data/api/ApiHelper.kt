package com.tk4dmitriy.playmuzio.data.api

class ApiHelper(private val apiService: ApiService) {
    suspend fun fetchCurrentUsersProfile() = apiService.fetchCurrentUsersProfile()
    suspend fun fetchFeaturedPlaylists(country: String) = apiService.fetchFeaturedPlaylists(country = country)
}