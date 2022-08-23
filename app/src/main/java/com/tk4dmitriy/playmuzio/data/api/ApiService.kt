package com.tk4dmitriy.playmuzio.data.api

import com.tk4dmitriy.playmuzio.data.model.endpoints.currentUsersProfile.CurrentUsersProfile
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.FeaturedPlaylists
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.NewReleases
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.TrackRecommendations
import com.tk4dmitriy.playmuzio.data.model.endpoints.userTopTracks.TopTracks
import com.tk4dmitriy.playmuzio.utils.Constants
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET(Constants.CURRENT_USERS_PROFILE)
    suspend fun fetchCurrentUsersProfile(): Response<CurrentUsersProfile>

    @GET(Constants.FEATURED_PLAYLISTS)
    suspend fun fetchFeaturedPlaylists(@Query("country") country: String): Response<FeaturedPlaylists>

    @GET(Constants.NEW_RELEASES)
    suspend fun fetchNewReleases(@Query("country") country: String): Response<NewReleases>

    @GET(Constants.USER_TOP_TRACKS)
    suspend fun fetchUserTopTracks(@Query("limit") limit: Int): Response<TopTracks>

    @GET(Constants.TRACK_RECOMMENDATIONS)
    suspend fun fetchTrackRecommendations(@Query("seed_tracks") seedTracks: String, @Query("time_range") timeRange: String): Response<TrackRecommendations>
}