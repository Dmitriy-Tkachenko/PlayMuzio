package com.tk4dmitriy.playmuzio.data.api

import com.tk4dmitriy.playmuzio.data.model.endpoints.album.Album
import com.tk4dmitriy.playmuzio.data.model.endpoints.browseCategories.BrowseCategories
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
    suspend fun fetchUserTopTracks(@Query("limit") limit: Int, @Query("time_range") timeRange: String): Response<TopTracks>

    @GET(Constants.TRACK_RECOMMENDATIONS)
    suspend fun fetchTrackRecommendationsByTracks(@Query("seed_tracks") seedTracks: String): Response<TrackRecommendations>

    @GET(Constants.TRACK_RECOMMENDATIONS)
    suspend fun fetchTrackRecommendationsByGenres(@Query("seed_genres") seedGenres: String): Response<TrackRecommendations>

    @GET(Constants.BROWSE_CATEGORIES)
    suspend fun fetchBrowseCategories(@Query("country") country: String): Response<BrowseCategories>

    @GET
    suspend fun fetchAlbum(@Url url: String): Response<Album>
}