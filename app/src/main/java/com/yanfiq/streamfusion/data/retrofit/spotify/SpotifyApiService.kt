package com.yanfiq.streamfusion.data.retrofit.spotify

import com.yanfiq.youcloudify.data.response.spotify.SpotifySearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/search")
    fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String,
        @Header("Authorization") token: String
    ): Call<SpotifySearchResponse>
}