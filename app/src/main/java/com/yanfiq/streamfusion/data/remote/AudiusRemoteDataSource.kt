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
    override suspend fun search(
        query: String,
        limit: Int,
        context: Context,
        apiStatus: ApiStatus,
        retryCount: Int,
        onProgress: (message: String) -> Unit,
        onResults: (List<Track>) -> Unit
    ) {
        if(AudiusEndpointUtil.getUsedEndpoint() != null){
            Log.d("AudiusSearch", "Start searching ${limit} ${query}")
            onProgress("Start search with keyword \'${query}\'")
            val api = AudiusEndpointUtil.getApiInstance()
            api?.searchTracks(query, limit)?.enqueue(object : Callback<AudiusResponse> {
                override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                    if (response.isSuccessful) {
                        val tracks = response.body()?.data ?: emptyList()
                        onResults(tracks.map { Track(it.id, it.title, it.user.name, it.duration, it.artwork.medium) })
                    } else {
                        Log.d("AudiusSearch", "Response not successful: ${response.errorBody()?.string()}")
                        onProgress("Response not successful: ${response.errorBody()?.string()}")
                        onResults(emptyList())
                    }
                }

                override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
                    Log.d("AudiusSearch", "API call failed: ${t.message}")
                    onProgress("API call failed: ${t.message}")
                    if(retryCount < 3){
                        apiStatus.updateAudiusApiReady(false)
                        CoroutineScope(Dispatchers.IO).launch {
                            onProgress("Re-fetching endpoints")
                            AudiusEndpointUtil.initialize(context, apiStatus)
                            onProgress("Trying again using ${AudiusEndpointUtil.getUsedEndpoint().toString()}")
                            search(query, limit, context, apiStatus, retryCount+1, onProgress = {message-> onProgress(message) }, onResults = {results -> onResults(results)})
                        }
                    }else{
                        onResults(emptyList())
                    }
                }
            })
        }else{
            apiStatus.updateAudiusApiReady(false)
            onProgress("API call failed")
            if(retryCount < 3){
                apiStatus.updateAudiusApiReady(false)
                CoroutineScope(Dispatchers.IO).launch {
                    onProgress("Re-fetching endpoints")
                    AudiusEndpointUtil.initialize(context, apiStatus)
                    onProgress("Trying again using ${AudiusEndpointUtil.getUsedEndpoint().toString()}")
                    search(query, limit, context, apiStatus, retryCount+1, onProgress = {message-> onProgress(message) }, onResults = {results -> onResults(results)})
                }
            }else{
                onResults(emptyList())
            }
        }
    }
}