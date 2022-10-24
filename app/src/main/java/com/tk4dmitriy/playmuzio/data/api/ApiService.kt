package com.tk4dmitriy.playmuzio.data.api

import com.tk4dmitriy.playmuzio.data.model.endpoints.album.Album
import com.tk4dmitriy.playmuzio.data.model.endpoints.currentUsersProfile.CurrentUsersProfile
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.FeaturedPlaylists
import com.tk4dmitriy.playmuzio.data.model.endpoints.matcherLyrics.MatcherLyrics
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.NewReleases
import com.tk4dmitriy.playmuzio.data.model.endpoints.playlist.Playlist
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackFromAlbum.TrackFromAlbum
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.Track
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

    @GET
    suspend fun fetchAlbum(@Url url: String): Response<Album>

    @GET
    suspend fun fetchPlaylist(@Url url: String, @Query("fields") fields: String =
        "description,images(height,url,width),name,tracks(total,items(track(album(images(height,url,width)),artists(name),name,duration_ms,href,preview_url)))"): Response<Playlist>

    @GET
    suspend fun fetchTrackFromAlbum(@Url url: String): Response<TrackFromAlbum>

    @GET
    suspend fun fetchTrackLyrics(@Url url: String, @Query("apikey") apiKey: String, @Query("q_track") track: String, @Query("q_artist") artist: String): Response<MatcherLyrics>
}