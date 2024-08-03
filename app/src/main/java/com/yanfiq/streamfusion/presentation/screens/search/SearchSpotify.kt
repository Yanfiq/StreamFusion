package com.yanfiq.streamfusion.presentation.screens.search

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.core.graphics.toColor
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.response.spotify.SpotifyResponse
import com.yanfiq.streamfusion.domain.model.spotify.Track
import com.yanfiq.streamfusion.data.retrofit.spotify.SpotifyApi
import com.yanfiq.streamfusion.data.viewmodel.SearchResult
import com.yanfiq.streamfusion.data.viewmodel.SearchStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun searchSpotify(
    context: Context,
    query: String,
    limit: Int,
    clientId: String,
    clientSecret: String,
    onResponse: (List<Track>) -> Unit
) {
    SpotifyApi.service.searchTracks(query, "track", limit).enqueue(object :
        Callback<SpotifyResponse> {
        override fun onResponse(
            call: Call<SpotifyResponse>,
            response: Response<SpotifyResponse>
        ) {
            if (response.isSuccessful) {
                val tracks = response.body()?.tracks?.items ?: emptyList()
                onResponse(tracks)
            } else {
                SpotifyApi.fetchAccessToken(context, clientId, clientSecret) { success ->
                    if (success) {
                        searchSpotify(context, query, limit, clientId, clientSecret) { response ->
                            onResponse(response)
                        }
                    }else{
                        onResponse(emptyList())
                    }
                }
                onResponse(emptyList())
            }
        }

        override fun onFailure(call: Call<SpotifyResponse>, t: Throwable) {
            onResponse(emptyList())
        }
    })
}

@Composable
fun SpotifySearchResult(searchResult: SearchResult, searchStatus: SearchStatus, context: Context) {
    val searchResults_spotify: List<Track> by searchResult.spotifySearchData.observeAsState(initial = emptyList())
    val isSearching: Boolean by searchStatus.spotifySearchStatus.observeAsState(initial = false)

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
    }else{
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults_spotify) { item ->
                val artists = item.artists.joinToString { it.name }
                ListItem(item.name, artists, item.album.images[0].url) {
                    val explicitIntent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:track:${item.id}:play"))
                    startActivity(context, explicitIntent, null)
                }
            }
        }
    }
}