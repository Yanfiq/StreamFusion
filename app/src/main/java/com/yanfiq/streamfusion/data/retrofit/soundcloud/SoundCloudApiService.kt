package com.yanfiq.streamfusion.data.retrofit.soundcloud

import com.yanfiq.youcloudify.data.response.soundcloud.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SoundCloudApiService {
    @GET("/tracks")
    fun searchTracks(
        @Query("client_id") clientId: String,
        @Query("q") query: String
    ): Call<List<Track>>
}
