package com.tk4dmitriy.playmuzio.data.repository

import com.tk4dmitriy.playmuzio.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun fetchCurrentUsersProfile() = apiHelper.fetchCurrentUsersProfile()
    suspend fun fetchFeaturedPlaylists(country: String) = apiHelper.fetchFeaturedPlaylists(country = country)
    suspend fun fetchNewReleases(country: String) = apiHelper.fetchNewReleases(country = country)
    suspend fun fetchUserTopTracks(limit: Int) = apiHelper.fetchUserTopTracks(limit = limit)
    suspend fun fetchTrackRecommendations(seedTracks: String, timeRange: String) = apiHelper.fetchTrackRecommendations(seedTracks = seedTracks, timeRange = timeRange)
}