package com.tk4dmitriy.playmuzio.data.api

class ApiHelper(private val apiService: ApiService) {
    suspend fun fetchCurrentUsersProfile() = apiService.fetchCurrentUsersProfile()
    suspend fun fetchFeaturedPlaylists(country: String) = apiService.fetchFeaturedPlaylists(country = country)
    suspend fun fetchNewReleases(country: String) = apiService.fetchNewReleases(country = country)
    suspend fun fetchUserTopTracks(limit: Int) = apiService.fetchUserTopTracks(limit = limit)
    suspend fun fetchTrackRecommendations(seedTracks: String, timeRange: String) = apiService.fetchTrackRecommendations(seedTracks = seedTracks, timeRange = timeRange)
}