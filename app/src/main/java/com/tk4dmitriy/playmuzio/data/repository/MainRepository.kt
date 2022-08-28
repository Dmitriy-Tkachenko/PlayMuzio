package com.tk4dmitriy.playmuzio.data.repository

import com.tk4dmitriy.playmuzio.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun fetchCurrentUsersProfile() = apiHelper.fetchCurrentUsersProfile()
    suspend fun fetchFeaturedPlaylists(country: String) = apiHelper.fetchFeaturedPlaylists(country = country)
    suspend fun fetchNewReleases(country: String) = apiHelper.fetchNewReleases(country = country)
    suspend fun fetchUserTopTracks(limit: Int, timeRange: String) = apiHelper.fetchUserTopTracks(limit = limit, timeRange = timeRange)
    suspend fun fetchTrackRecommendationsByTracks(seedTracks: String) = apiHelper.fetchTrackRecommendationsByTracks(seedTracks = seedTracks)
    suspend fun fetchTrackRecommendationsByGenres(seedGenres: String) = apiHelper.fetchTrackRecommendationsByGenres(seedGenres = seedGenres)
    suspend fun fetchBrowseCategories(country: String) = apiHelper.fetchBrowseCategories(country = country)
    suspend fun fetchAlbum(url: String) = apiHelper.fetchAlbum(url = url)
}