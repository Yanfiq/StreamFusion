package com.yanfiq.streamfusion.data.retrofit.audius

import com.yanfiq.streamfusion.data.response.audius.EndpointsResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface AudiusEndpointsApi {
    @GET("/")
    fun getEndpoints(): Call<EndpointsResponse>

    companion object {
        private const val BASE_URL = "https://api.audius.co/"

        fun create(): AudiusEndpointsApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(AudiusEndpointsApi::class.java)
        }
    }
}
