package com.yanfiq.streamfusion.data.retrofit.youtube

import com.yanfiq.streamfusion.data.response.youtube.YouTubeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {
    @GET("search")
    fun searchVideos(
        @Query("part") part: String,
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): Call<YouTubeResponse>
}