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

class GetTrendingUseCase(
    private val audiusRepository: AudiusRepository = AudiusRemoteDataSource(),
    private val soundcloudRepository: SoundcloudRepository = SoundcloudRemoteDataSource(),
    private val spotifyRepository: SpotifyRepository = SpotifyRemoteDataSource(),
    private val youtubeRepository: YoutubeRepository = YoutubeRemoteDataSource()
) {
    suspend fun getAudiusTrending(limit: Int, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit){
        audiusRepository.getTrending(
            limit,
            onResults = { results ->
                onResults(results)
            },
            onProgress = { message ->
                onProgress(message)
            }
        )
    }

    suspend fun getSoundcloudTrending(limit: Int, context: Context, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit){
        soundcloudRepository.getTrending(
            limit = limit,
            context = context,
            onResults = { results ->
                onResults(results)
            },
            onProgress = { message ->
                onProgress(message)
            }
        )
    }

    suspend fun getSpotifyTrending(limit: Int, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit){

    }

    suspend fun getYoutubeTrending(limit: Int, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit){
        
    }
}