package com.yanfiq.streamfusion.data.retrofit.youtube

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object YouTubeApi {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    val retrofitService: YoutubeApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(YoutubeApiService::class.java)
    }
}