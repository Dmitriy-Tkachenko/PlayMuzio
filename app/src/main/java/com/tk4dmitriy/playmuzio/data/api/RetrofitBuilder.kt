package com.tk4dmitriy.playmuzio.data.api

import com.tk4dmitriy.playmuzio.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    private var retrofit: Retrofit? = null

    fun getRetrofit(url: String): Retrofit {
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${Constants.TOKEN}")
                .build()
            chain.proceed(newRequest)
        }.build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    val apiService: ApiService = getRetrofit(Constants.BASE_URL_API).create(ApiService::class.java)
}