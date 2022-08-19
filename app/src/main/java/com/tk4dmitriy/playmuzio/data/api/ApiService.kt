package com.tk4dmitriy.playmuzio.data.api

import com.tk4dmitriy.playmuzio.data.model.endpoints.currentUsersProfile.CurrentUsersProfile
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.FeaturedPlaylists
import com.tk4dmitriy.playmuzio.utils.Constants
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET(Constants.CURRENT_USERS_PROFILE)
    suspend fun fetchCurrentUsersProfile(): Response<CurrentUsersProfile>

    @GET(Constants.FEATURED_PLAYLISTS)
    suspend fun fetchFeaturedPlaylists(@Query("country") country: String): Response<FeaturedPlaylists>
}