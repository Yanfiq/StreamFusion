package com.yanfiq.streamfusion.data.retrofit.audius

import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AudiusApiService {
    @GET("v1/tracks/trending")
    fun getTrendingTracks(
        @Query("limit") limit: Int = 10
    ): Call<AudiusResponse>

    @GET("v1/tracks/search")
    fun searchTracks(
        @Query("query") query: String,
        @Query("limit") limit: Int = 10
    ): Call<AudiusResponse>

    @GET("v1/tracks/{track_id}/stream")
    fun streamTrack(@Path("track_id") trackId: String, @Query("app_name") appName: String = "StreamFusion"): Call<ResponseBody>
}
