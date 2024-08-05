package com.yanfiq.streamfusion.data.remote

import android.content.Context
import android.util.Log
import com.yanfiq.streamfusion.data.repositories.YoutubeRepository
import com.yanfiq.streamfusion.data.retrofit.youtube.YouTubeApi
import com.yanfiq.streamfusion.domain.model.Track
import com.yanfiq.streamfusion.domain.model.youtube.Video
import com.yanfiq.streamfusion.utils.ISODurationToSeconds
import kotlinx.coroutines.coroutineScope

class YoutubeRemoteDataSource: YoutubeRepository {
    override suspend fun search(
        query: String,
        limit: Int,
        context: Context,
        apiKey: String,
        onResults: (List<Track>) -> Unit
    ) {
        coroutineScope {
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

                            val tracks = videoItems.map { videoItem ->
                                val detailsItem = videoDetailsItems.find { it.id == videoItem.id.videoId }
                                Track(
                                    trackId = videoItem.id.videoId,
                                    tractTitle = videoItem.snippet.title,
                                    trackArtist = videoItem.snippet.channelTitle,
                                    durationInSeconds = ISODurationToSeconds(detailsItem?.contentDetails?.duration ?: "0S"),
                                    trackArtworkUrl = videoItem.snippet.thumbnails.medium.url
                                )
                            }
                            onResults(tracks)
                        } else { //details response not successful
                            Log.d("youtubeSearch", "Response not successful: ${detailsResponse.errorBody()?.string()}")
                            onResults(emptyList())
                        }
                    }
                } else { //search response not successful
                    Log.d("youtubeSearch", "API call failed: ${searchResponse.message()}")
                    onResults(emptyList())
                }
            } catch (e: Exception) { //API call failed
                Log.d("youtubeSearch", "API call failed: ${e}")
                onResults(emptyList())
            }
        }
    }
}