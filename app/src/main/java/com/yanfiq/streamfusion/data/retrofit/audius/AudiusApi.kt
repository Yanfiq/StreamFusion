package com.yanfiq.streamfusion.data.retrofit.audius

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AudiusApi {
    fun retrofitService(baseUrl: String): AudiusApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return  retrofit.create(AudiusApiService::class.java)
    }
}
