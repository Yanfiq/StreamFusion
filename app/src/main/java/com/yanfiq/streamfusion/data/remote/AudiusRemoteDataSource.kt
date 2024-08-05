package com.yanfiq.streamfusion.data.remote

import android.content.Context
import android.util.Log
import com.yanfiq.streamfusion.data.repositories.AudiusRepository
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import com.yanfiq.streamfusion.domain.model.Track
import com.yanfiq.streamfusion.presentation.viewmodels.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AudiusRemoteDataSource: AudiusRepository {
    override suspend fun search(query: String, limit: Int, context: Context, apiStatus: ApiStatus, onResults: (List<Track>) -> Unit) {
        if(AudiusEndpointUtil.getUsedEndpoint() != null){
            Log.d("AudiusSearch", "Start searching ${query} with ${limit} as the limit")
            val api = AudiusEndpointUtil.getApiInstance()
            api?.searchTracks(query, limit)?.enqueue(object : Callback<AudiusResponse> {
                override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                    if (response.isSuccessful) {
                        val tracks = response.body()?.data ?: emptyList()
                        onResults(tracks.map { Track(it.id, it.title, it.user.name, it.duration, it.artwork.medium) })
                    } else {
                        Log.d("AudiusSearch", "Response not successful: ${response.errorBody()?.string()}")
                        onResults(emptyList())
                    }
                }

                override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
                    Log.d("AudiusSearch", "API call failed: ${t.message}")
                    apiStatus.updateAudiusApiReady(false)
                    CoroutineScope(Dispatchers.IO).launch {
                        AudiusEndpointUtil.initialize(context, apiStatus)
                        search(query, limit, context, apiStatus, onResults = {results -> onResults(results)})
                    }
                    onResults(emptyList())
                }
            })
        }
    }
}