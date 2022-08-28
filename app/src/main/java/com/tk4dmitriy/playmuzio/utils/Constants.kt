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
    const val BROWSE_CATEGORIES = "v1/browse/categories"

    // For request USER_TOP_TRACKS or USER_TOP_ARTISTS
    const val TIME_RANGE_LONG_TERM = "long_term" // data for several a years
    const val TIME_RANGE_MEDIUM_TERM = "medium_term" // data for half a years
    const val TIME_RANGE_SHORT_TERM = "short_term" // data for last a month\
    const val LIMIT = 5
}