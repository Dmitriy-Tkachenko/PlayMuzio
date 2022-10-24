package com.tk4dmitriy.playmuzio.utils

object Constants {
    var TOKEN = ""

    const val BASE_URL_API = "https://api.spotify.com/"
    const val CURRENT_USERS_PROFILE = "v1/me"
    const val FEATURED_PLAYLISTS = "v1/browse/featured-playlists"
    const val NEW_RELEASES = "v1/browse/new-releases"
    const val USER_TOP_TRACKS = "v1/me/top/tracks"
    const val TRACK_RECOMMENDATIONS = "v1/recommendations"
    const val FOLLOWED_ARTISTS = "v1/me/following?type=artist"

    const val API_URL_MUSIX_MATCH = "https://api.musixmatch.com/ws/1.1/"
    const val API_KEY = "721e510c81e641205bdbb7a99a656063"
    const val TRACK_LYRICS = "matcher.lyrics.get"
}