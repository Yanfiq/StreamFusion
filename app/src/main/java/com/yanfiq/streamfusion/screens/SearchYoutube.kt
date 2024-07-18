package com.yanfiq.streamfusion.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.yanfiq.streamfusion.data.response.youtube.Video
import com.yanfiq.streamfusion.data.response.youtube.VideoItem
import com.yanfiq.streamfusion.data.retrofit.youtube.YouTubeApi
import com.yanfiq.streamfusion.data.viewmodel.SearchResult
import com.yanfiq.streamfusion.data.viewmodel.SearchStatus
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.ui.youtube.VideoAdapter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

suspend fun searchYouTube(context: Context, query: String, limit: Int, apiKey: String, onResponse: (List<Video>) -> Unit) {
    coroutineScope {
        try {
            val youTubeApiService = YouTubeApi.getApiInstance(context, apiKey)
            val searchResponse = youTubeApiService.searchVideos("snippet", query, "video", limit)
            if (searchResponse.isSuccessful) {
                val videoItems = searchResponse.body()?.items
                val videoIds = videoItems?.joinToString(",") { it.id.videoId }
                Log.d("searchYoutube", videoIds.toString())

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
                    } else {
                        Log.d("YoutubeSearch", "Response not successful: ${detailsResponse.errorBody()?.string()}")
                        onResponse(emptyList())
                    }
                }
            } else {
                Log.d("YoutubeSearch", "API call failed: ${searchResponse.message()}")
                onResponse(emptyList())
            }
        } catch (e: Exception) {
            Log.d("YoutubeSearch", "API call failed: ${e}")
            onResponse(emptyList())
        }
    }
}

@Composable
fun YoutubeSearchResult(searchResult: SearchResult, searchStatus: SearchStatus, context: Context, searchQuery: String) {
    val searchResults by searchResult.youtubeSearchData.observeAsState(initial = emptyList())
    val isSearching by searchStatus.youtubeSearchStatus.observeAsState(initial = false)
    val maxResult by (LocalContext.current.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RESULT_PER_SEARCH] ?: 10f
    }).collectAsState(initial = 10f)
    val youtubeApiKey by (LocalContext.current.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.YOUTUBE_API_KEY] ?: ""
    }).collectAsState(initial = "")

    LaunchedEffect(isSearching, youtubeApiKey) {
        if (isSearching && youtubeApiKey.isNotEmpty()) {
            Log.d("YoutubeSearch", "Starting search $maxResult $searchQuery using $youtubeApiKey")
            searchYouTube(context, searchQuery, maxResult.toInt(), youtubeApiKey) { result ->
                searchResult.updateYoutubeSearchData(result)
                searchStatus.updateYoutubeSearchStatus(false)
            }
        }
    }

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
//                    val explicitIntent = Intent(
//                        context,
//                        com.yanfiq.streamfusion.screens.PlayYou
//                    )
//                    explicitIntent.putExtra("TRACK_TITLE", item.title)
//                    explicitIntent.putExtra("TRACK_ARTIST", item.user)
//                    explicitIntent.putExtra("TRACK_ARTWORK", item.artwork_url)
//                    explicitIntent.putExtra("TRACK_URL", item.stream_url)
//                    startActivity(context, explicitIntent, null)
                }
            }
        }
    }
}