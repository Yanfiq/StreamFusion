package com.yanfiq.streamfusion.presentation.screens.search

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.yanfiq.streamfusion.domain.model.youtube.Video
import com.yanfiq.streamfusion.data.retrofit.youtube.YouTubeApi
import com.yanfiq.streamfusion.data.viewmodel.SearchResult
import com.yanfiq.streamfusion.data.viewmodel.SearchStatus
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.presentation.screens.settings.PreferencesKeys
import com.yanfiq.streamfusion.presentation.screens.player.PlayYoutubeActivity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map

suspend fun searchYouTube(context: Context, query: String, limit: Int, apiKey: String, onResponse: (List<Video>) -> Unit) {
    coroutineScope {
        if(apiKey != ""){
            try {
                Log.d("youtubeSearch", "Starting search $limit $query using $apiKey")
                val youTubeApiService = YouTubeApi.getApiInstance(context, apiKey)
                val searchResponse = youTubeApiService.searchVideos("snippet", query, "video", limit)
                if (searchResponse.isSuccessful) {
                    val videoItems = searchResponse.body()?.items
                    val videoIds = videoItems?.joinToString(",") { it.id.videoId }

                    val detailsResponse = videoIds?.let {
                        youTubeApiService.getVideoDetails("contentDetails",
                            it)
                    }
                    if (detailsResponse != null) {
                        if (detailsResponse.isSuccessful) {
                            val videoDetailsItems = detailsResponse.body()?.items ?: emptyList()

                            val videos = videoItems.map { videoItem ->
                                val detailsItem = videoDetailsItems.find { it.id == videoItem.id.videoId }
                                Video(
                                    id = videoItem.id.videoId,
                                    title = videoItem.snippet.title,
                                    description = videoItem.snippet.description,
                                    thumbnailUrl = videoItem.snippet.thumbnails.default.url,
                                    channel = videoItem.snippet.channelTitle,
                                    duration = detailsItem?.contentDetails?.duration ?: ""
                                )
                            }
                            onResponse(videos)
                        } else { //details response not successful
                            Log.d("youtubeSearch", "Response not successful: ${detailsResponse.errorBody()?.string()}")
                            onResponse(emptyList())
                        }
                    }
                } else { //search response not successful
                    Log.d("youtubeSearch", "API call failed: ${searchResponse.message()}")
                    onResponse(emptyList())
                }
            } catch (e: Exception) { //API call failed
                Log.d("youtubeSearch", "API call failed: ${e}")
                onResponse(emptyList())
            }
        }
    }
}

@Composable
fun YoutubeSearchResult(searchResult: SearchResult, searchStatus: SearchStatus, context: Context) {
    val searchResults by searchResult.youtubeSearchData.observeAsState(initial = emptyList())
    val isSearching by searchStatus.youtubeSearchStatus.observeAsState(initial = false)

    val maxResult by (LocalContext.current.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RESULT_PER_SEARCH] ?: 10f
    }).collectAsState(initial = 10f)

    if (isSearching){
        Box(
            modifier = Modifier
                .width((LocalConfiguration.current.screenWidthDp).dp)
                .height((LocalConfiguration.current.screenHeightDp).dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults) { item ->
                ListItem(item.title, item.channel, item.thumbnailUrl ?: "") {
                    val explicitIntent = Intent(
                        context,
                        PlayYoutubeActivity::class.java
                    )
                    explicitIntent.putExtra("VIDEO_ID", item.id)
                    explicitIntent.putExtra("VIDEO_TITLE", item.title)
                    explicitIntent.putExtra("VIDEO_CREATOR", item.channel)
                    explicitIntent.putExtra("VIDEO_ARTWORK", item.thumbnailUrl)
                    explicitIntent.putExtra("VIDEO_DURATION", item.duration)
                    startActivity(context, explicitIntent, null)
                }
            }
        }
    }
}