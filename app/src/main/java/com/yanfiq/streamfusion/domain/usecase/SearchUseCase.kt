package com.yanfiq.streamfusion.domain.usecase

import android.content.Context
import com.yanfiq.streamfusion.data.remote.AudiusRemoteDataSource
import com.yanfiq.streamfusion.data.remote.SoundcloudRemoteDataSource
import com.yanfiq.streamfusion.data.remote.SpotifyRemoteDataSource
import com.yanfiq.streamfusion.data.remote.YoutubeRemoteDataSource
import com.yanfiq.streamfusion.data.repositories.AudiusRepository
import com.yanfiq.streamfusion.data.repositories.SoundcloudRepository
import com.yanfiq.streamfusion.data.repositories.SpotifyRepository
import com.yanfiq.streamfusion.data.repositories.YoutubeRepository
import com.yanfiq.streamfusion.domain.model.Track
import com.yanfiq.streamfusion.presentation.viewmodels.ApiStatus

class SearchUseCase(
    private val audiusRepository: AudiusRepository = AudiusRemoteDataSource(),
    private val soundcloudRepository: SoundcloudRepository = SoundcloudRemoteDataSource(),
    private val spotifyRepository: SpotifyRepository = SpotifyRemoteDataSource(),
    private val youtubeRepository: YoutubeRepository = YoutubeRemoteDataSource()
) {
    suspend fun searchAudius(query: String, limit: Int, context: Context, apiStatus: ApiStatus, retryCount: Int, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit) {
        audiusRepository.search(
            query,
            limit,
            context,
            apiStatus,
            retryCount,
            onProgress = {message ->
                onProgress(message)
            },
            onResults = {results ->
                onResults(results)
            }
        )
    }

    suspend fun searchSoundcloud(query: String, limit: Int, context: Context, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit) {
        soundcloudRepository.search(
            query,
            limit,
            context,
            onProgress = {message ->
                onProgress(message)
            },
            onResults = {results ->
                onResults(results)
            }
        )
    }

    suspend fun searchSpotify(query: String, limit: Int, context: Context, clientId: String, clientSecret: String, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit) {
        spotifyRepository.search(
            query,
            limit,
            context,
            clientId,
            clientSecret,
            onProgress = {message ->
                onProgress(message)
            },
            onResults = {results ->
                onResults(results)
            }
        )
    }

    suspend fun searchYoutube(query: String, limit: Int, context: Context, apiKey: String, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit) {
        youtubeRepository.search(
            query,
            limit,
            context,
            apiKey,
            onProgress = {message ->
                onProgress(message)
            },
            onResults = {results ->
                onResults(results)
            }
        )
    }
}