package com.yanfiq.streamfusion.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.preference.PreferenceManager
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import com.yanfiq.streamfusion.data.response.audius.Track
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import com.yanfiq.streamfusion.data.viewmodel.SearchResult
import com.yanfiq.streamfusion.data.viewmodel.SearchStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun search_audius(query: String, context: Context, onResults: (List<Track>) -> Unit) {
    //audius
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val limit = sharedPreferences.getString("result_per_query", "10")!!.toInt()
    if(AudiusEndpointUtil.getUsedEndpoint() != null){
        Log.d("AudiusSearch", AudiusEndpointUtil.getUsedEndpoint().toString())
        val api = AudiusEndpointUtil.getApiInstance()
        api?.searchTracks(query, limit)?.enqueue(object : Callback<AudiusResponse> {
            override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.data ?: emptyList()
                    onResults(tracks)
                } else {
                    Log.d("AudiusSearch", "Response not successful: ${response.errorBody()?.string()}")
                    onResults(emptyList())
                }
            }

            override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
                Log.d("AudiusSearch", "API call failed: ${t.message}")
                onResults(emptyList())
            }
        })
    }
}

@Composable
fun AudiusSearchResult(searchResult: SearchResult = viewModel(), searchStatus: SearchStatus = viewModel(), context: Context, searchQuery: String) {
    val searchResults_audius: List<Track> by searchResult.audiusSearchData.observeAsState(initial = emptyList())
    val isSearching by searchStatus.audiusSearchStatus.observeAsState(initial = false)

    if(isSearching){
        search_audius(searchQuery, context){ result ->
            searchResult.updateAudiusSearchData(result)
            searchStatus.updateAudiusSearchStatus(false)
        }
    }

    if (isSearching){
        Box(
            modifier = Modifier
                .width((LocalConfiguration.current.screenWidthDp).dp)
                .height((LocalConfiguration.current.screenHeightDp).dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }else{
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults_audius) { item ->
                ListItem(item.title, item.user.name, item.artwork.small) {
                    val explicitIntent = Intent(context, PlayAudiusActivity::class.java)
                    explicitIntent.putExtra("TRACK_ID", item.id)
                    explicitIntent.putExtra("TRACK_TITLE", item.title)
                    explicitIntent.putExtra("TRACK_ARTIST", item.user.name)
                    explicitIntent.putExtra("TRACK_ARTWORK", item.artwork.large)
                    startActivity(context, explicitIntent, null)
                }
            }
        }
    }
}