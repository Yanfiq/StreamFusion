package com.yanfiq.streamfusion.data.retrofit.soundcloud

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SoundCloudApi {
    private const val BASE_URL = "https://api.soundcloud.com/"

    val retrofitService: SoundCloudApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SoundCloudApiService::class.java)
    }
}
