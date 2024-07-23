package com.yanfiq.streamfusion.data.retrofit.spotify

import com.yanfiq.streamfusion.data.response.spotify.SpotifyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/search")
    fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("limit") limit: Int = 10
    ): Call<SpotifyResponse>
}
