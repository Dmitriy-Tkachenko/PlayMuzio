package com.tk4dmitriy.playmuzio.data.api

class ApiHelper(private val apiService: ApiService) {
    suspend fun fetchCurrentUsersProfile() = apiService.fetchCurrentUsersProfile()

    suspend fun fetchFeaturedPlaylists(country: String) = apiService.fetchFeaturedPlaylists(country = country)

    suspend fun fetchNewReleases(country: String) = apiService.fetchNewReleases(country = country)

    suspend fun fetchUserTopTracks(limit: Int, timeRange: String) = apiService.fetchUserTopTracks(limit = limit, timeRange = timeRange)

    suspend fun fetchTrackRecommendationsByTracks(seedTracks: String) = apiService.fetchTrackRecommendationsByTracks(seedTracks = seedTracks)

    suspend fun fetchTrackRecommendationsByGenres(seedGenres: String) = apiService.fetchTrackRecommendationsByGenres(seedGenres = seedGenres)

    suspend fun fetchBrowseCategories(country: String) = apiService.fetchBrowseCategories(country = country)

    suspend fun fetchAlbum(url: String) = apiService.fetchAlbum(url = url)
}