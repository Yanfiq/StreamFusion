package com.yanfiq.streamfusion.data.remote

import android.content.Context
import android.util.Log
import com.yanfiq.streamfusion.data.repositories.SpotifyRepository
import com.yanfiq.streamfusion.data.response.spotify.SpotifyResponse
import com.yanfiq.streamfusion.data.retrofit.spotify.SpotifyApi
import com.yanfiq.streamfusion.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpotifyRemoteDataSource: SpotifyRepository {
    override suspend fun search(
        query: String,
        limit: Int,
        context: Context,
        clientId: String,
        clientSecret: String,
        onProgress: (String) -> Unit,
        onResults: (List<Track>) -> Unit
    ) {
        Log.d("spotifySearch", "Starting search $limit $query using $clientId and $clientSecret")
        onProgress("Start search with keyword \'${query}\'")
        SpotifyApi.service.searchTracks(query, "track", limit).enqueue(object :
            Callback<SpotifyResponse> {
            override fun onResponse(
                call: Call<SpotifyResponse>,
                response: Response<SpotifyResponse>
            ) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.tracks?.items ?: emptyList()
                    onResults(tracks.map { Track(it.id, it.name, it.artists.joinToString { it.name }, it.duration_ms/1000, it.album.images[0].url) })
                } else {
                    onProgress("Re-fetch access token")
                    SpotifyApi.fetchAccessToken(context, clientId, clientSecret) { success ->
                        if (success) {
                            CoroutineScope(Dispatchers.IO).launch {
                                search(query, limit, context, clientId, clientSecret,
                                    onProgress = {message ->
                                        onProgress(message)
                                    },
                                    onResults = {results ->
                                        onResults(results)
                                    }
                                )
                            }
                        }else{
                            onResults(emptyList())
                        }
                    }
                    onResults(emptyList())
                }
            }

            override fun onFailure(call: Call<SpotifyResponse>, t: Throwable) {
                onResults(emptyList())
            }
        })
    }
}