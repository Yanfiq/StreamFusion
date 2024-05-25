package com.yanfiq.streamfusion.data.retrofit.spotify

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpotifyApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: SpotifyApiService = retrofit.create(SpotifyApiService::class.java)
}