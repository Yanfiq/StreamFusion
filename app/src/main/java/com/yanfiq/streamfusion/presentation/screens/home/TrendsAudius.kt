package com.yanfiq.streamfusion.presentation.screens.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import com.yanfiq.streamfusion.presentation.viewmodels.ApiStatus
import com.yanfiq.streamfusion.domain.model.audius.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getAudiusTrending(limit: Int, context: Context, apiStatus: ApiStatus, onResults: (List<Track>) -> Unit) {
    //audius
    if(AudiusEndpointUtil.getUsedEndpoint() != null){
        val api = AudiusEndpointUtil.getApiInstance()
        api?.getTrendingTracks(limit)?.enqueue(object : Callback<AudiusResponse> {
            override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.data ?: emptyList()
                    onResults(tracks)
                } else {
                    Log.d("AudiusTrends", "Response not successful: ${response.errorBody()?.string()}")
                    onResults(emptyList())
                }
            }

            override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
                Log.d("AudiusTrends", "API call failed: ${t.message}")
                apiStatus.updateAudiusApiReady(false)
                CoroutineScope(Dispatchers.IO).launch {
                    AudiusEndpointUtil.initialize(context, apiStatus)
                    getAudiusTrending(limit, context, apiStatus, onResults = {result -> onResults(result)})
                }
                onResults(emptyList())
            }
        })
    }
}

@Composable
fun AudiusTrends(audiusTrends: List<com.yanfiq.streamfusion.domain.model.audius.Track>){
    Column {
        Text(
            text = "Audius Trending",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        LazyColumn {
            items(audiusTrends) { item ->
//                ListItem(item.title, item.user.name, item.artwork.small) {
//                    val explicitIntent = Intent(context, PlayAudiusActivity::class.java)
//                    explicitIntent.putExtra("TRACK_ID", item.id)
//                    explicitIntent.putExtra("TRACK_TITLE", item.title)
//                    explicitIntent.putExtra("TRACK_ARTIST", item.user.name)
//                    explicitIntent.putExtra("TRACK_ARTWORK", item.artwork.large)
//                    startActivity(context, explicitIntent, null)
//                }
            }
        }
    }
}