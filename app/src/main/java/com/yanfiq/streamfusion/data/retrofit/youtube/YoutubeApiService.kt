package com.yanfiq.streamfusion.data.retrofit.youtube

import com.yanfiq.streamfusion.data.response.youtube.VideoDetailsResponse
import com.yanfiq.streamfusion.data.response.youtube.YouTubeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String,
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("maxResults") maxResults: Int
    ): Response<YouTubeResponse>

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String,
        @Query("id") ids: String
    ): Response<VideoDetailsResponse>
}
