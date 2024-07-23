package com.yanfiq.streamfusion.data.retrofit.audius

import android.util.Log
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import com.yanfiq.streamfusion.domain.model.audius.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AudiusApi {
    private var apiService: AudiusApiService? = null
    private lateinit var trends: List<Track>

    fun retrofitService(baseUrl: String): AudiusApiService {
        if(apiService == null){
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = retrofit.create(AudiusApiService::class.java)
        }
        return apiService!!
    }

    fun fetchTrendingTracks(){
        apiService?.getTrendingTracks()?.enqueue(object : Callback<AudiusResponse> {
            override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.data ?: emptyList()
                    trends = tracks
                } else {
                    Log.d("AudiusTrends", "Response not successful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
                Log.d("AudiusTrends", "API call failed: ${t.message}")
            }
        })
    }
}
