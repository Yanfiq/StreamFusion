package com.yanfiq.streamfusion.presentation.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.domain.model.Track
import com.yanfiq.streamfusion.domain.usecase.SearchUseCase
import com.yanfiq.streamfusion.presentation.screens.player.PlayAudiusActivity
import com.yanfiq.streamfusion.presentation.screens.player.PlaySoundcloudActivity
import com.yanfiq.streamfusion.presentation.screens.player.PlayYoutubeActivity
import com.yanfiq.streamfusion.presentation.screens.search.SearchData
import com.yanfiq.streamfusion.presentation.screens.search.SearchScreenData
import com.yanfiq.streamfusion.presentation.screens.search.StreamingPlatform
import com.yanfiq.streamfusion.presentation.screens.settings.PreferencesKeys
import com.yanfiq.streamfusion.utils.getHiddenMessage
import com.yanfiq.streamfusion.utils.loadImageFromDrawable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel (
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _searchScreenData = MutableStateFlow(SearchScreenData())
    val searchScreenData: StateFlow<SearchScreenData> = _searchScreenData

    suspend fun startSearch(searchQuery: String, context: Context, apiStatus: ApiStatus) {
        val limit = (context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.RESULT_PER_SEARCH] ?: 10f
        }).first().toInt()

        val spotifyHiddenThings = getHiddenMessage(loadImageFromDrawable(context, R.drawable.spotifylogo_nodpi))

        val spotifyClientId = (context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SPOTIFY_CLIENT_ID] ?: ""
        }).first().toString().takeIf { it != "" } ?: spotifyHiddenThings.substring(0, spotifyHiddenThings.indexOf('|'))

        val spotifyClientSecret = (context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SPOTIFY_CLIENT_SECRET] ?: ""
        }).first().toString().takeIf { it != "" } ?: spotifyHiddenThings.substring(spotifyHiddenThings.indexOf('|')+1)

        val youtubeKey = (context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SPOTIFY_CLIENT_ID] ?: ""
        }).first().toString().takeIf { it != "" } ?: getHiddenMessage(loadImageFromDrawable(context, R.drawable.youtubelogo_text_nodpi))

        viewModelScope.launch {
            _searchScreenData.update { it.copy(audiusSearchData = SearchData(isLoading = true)) }
            _searchScreenData.update { it.copy(soundcloudSearchData = SearchData(isLoading = true)) }
            _searchScreenData.update { it.copy(spotifySearchData = SearchData(isLoading = true)) }
            _searchScreenData.update { it.copy(youtubeSearchData = SearchData(isLoading = true)) }
        }

        viewModelScope.launch {
            searchUseCase.searchAudius(searchQuery, limit, context, apiStatus, 0,
                onProgress = { message ->
                    _searchScreenData.update { it.copy(audiusSearchData = _searchScreenData.value.audiusSearchData.copy(message = message)) }
                },
                onResults = { results ->
                    _searchScreenData.update { it.copy(audiusSearchData = SearchData(result = results, false)) }
                }
            )
        }

        viewModelScope.launch {
            searchUseCase.searchSoundcloud(searchQuery, limit, context,
                onProgress = { message ->
                    _searchScreenData.update { it.copy(soundcloudSearchData = _searchScreenData.value.soundcloudSearchData.copy(message = message)) }
                },
                onResults = { results ->
                    _searchScreenData.update { it.copy(soundcloudSearchData = SearchData(result = results, isLoading = false)) }
                }
            )
        }

        viewModelScope.launch {
            searchUseCase.searchSpotify(searchQuery, limit, context, spotifyClientId, spotifyClientSecret,
                onProgress = { message ->
                    _searchScreenData.update { it.copy(spotifySearchData = _searchScreenData.value.spotifySearchData.copy(message = message)) }
                },
                onResults = { results ->
                    _searchScreenData.update { it.copy(spotifySearchData = SearchData(result = results, isLoading = false)) }
                }
            )
        }

        viewModelScope.launch {
            searchUseCase.searchYoutube(searchQuery, limit, context, youtubeKey,
                onProgress = { message ->
                    _searchScreenData.update { it.copy(youtubeSearchData = _searchScreenData.value.youtubeSearchData.copy(message = message)) }
                },
                onResults = { results ->
                    _searchScreenData.update { it.copy(youtubeSearchData = SearchData(result = results, isLoading = false)) }
                }
            )
        }
    }

    fun playTrack(context: Context, track: Track, platform: StreamingPlatform) {

        when(platform){
            StreamingPlatform.AUDIUS -> {
                val explicitIntent = Intent(context, PlayAudiusActivity::class.java)
                explicitIntent.putExtra("TRACK_ID", track.trackId)
                explicitIntent.putExtra("TRACK_TITLE", track.tractTitle)
                explicitIntent.putExtra("TRACK_ARTIST", track.trackArtist)
                explicitIntent.putExtra("TRACK_ARTWORK", track.trackArtworkUrl)
                startActivity(context, explicitIntent, null)
            }
            StreamingPlatform.SOUNDCLOUD -> {
                val explicitIntent = Intent(context, PlaySoundcloudActivity::class.java)
                explicitIntent.putExtra("TRACK_TITLE", track.tractTitle)
                explicitIntent.putExtra("TRACK_ARTIST", track.trackArtist)
                explicitIntent.putExtra("TRACK_ARTWORK", track.trackArtworkUrl)
                explicitIntent.putExtra("TRACK_URL", track.trackId)
                startActivity(context, explicitIntent, null)
            }
            StreamingPlatform.SPOTIFY -> {
                val explicitIntent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:track:${track.trackId}:play"))
                startActivity(context, explicitIntent, null)
            }
            StreamingPlatform.YOUTUBE -> {
                val explicitIntent = Intent(context, PlayYoutubeActivity::class.java)
                explicitIntent.putExtra("VIDEO_ID", track.trackId)
                explicitIntent.putExtra("VIDEO_TITLE", track.tractTitle)
                explicitIntent.putExtra("VIDEO_CREATOR", track.trackArtist)
                explicitIntent.putExtra("VIDEO_ARTWORK", track.trackArtworkUrl)
                explicitIntent.putExtra("VIDEO_DURATION", track.durationInSeconds)
                startActivity(context, explicitIntent, null)
            }
        }
    }
}