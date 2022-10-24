package com.tk4dmitriy.playmuzio.data.api

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tk4dmitriy.playmuzio.data.model.endpoints.matcherLyrics.Body
import com.tk4dmitriy.playmuzio.utils.ConnectionLiveData
import com.tk4dmitriy.playmuzio.utils.Constants
import com.tk4dmitriy.playmuzio.utils.DefaultOnDataMismatchAdapter
import com.tk4dmitriy.playmuzio.utils.NullToEmptyStringAdapter
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.lang.IllegalStateException

class RetrofitBuilder private constructor(context: Context) {
    private val onlineInterceptor = Interceptor { chain ->
        val response: Response = chain.proceed(chain.request())
        val maxAge = 60
        response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .removeHeader("Pragma")
            .build()
    }

    private val offlineInterceptor = Interceptor { chain ->
        var request: Request = chain.request()
        if (!isInternetAvailable(context.applicationContext)) {
            val maxStale = 60 * 60 * 24 * 30
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale$maxStale")
                .removeHeader("Pragma")
                .build()
        }
        return@Interceptor chain.proceed(request)
    }

    private val authorizationInterceptor = Interceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${Constants.TOKEN}")
            .build()
        return@Interceptor chain.proceed(newRequest)
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    private val cacheSize: Long = 100 * 1024 * 1024
    private val cache: Cache = Cache(context.applicationContext.cacheDir, cacheSize)

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authorizationInterceptor)
        .addInterceptor(offlineInterceptor)
        .addNetworkInterceptor(onlineInterceptor)
        .cache(cache)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL_API)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(
            Moshi.Builder()
                .add(NullToEmptyStringAdapter())
                .add(DefaultOnDataMismatchAdapter.newFactory(Body::class.java, null))
                .add(KotlinJsonAdapterFactory())
                .build()))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    companion object {
        private var INSTANCE: RetrofitBuilder? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = RetrofitBuilder(context)
        }

        fun get(): RetrofitBuilder {
            return INSTANCE ?: throw IllegalStateException("Retrofit must be initialized")
        }
    }
}