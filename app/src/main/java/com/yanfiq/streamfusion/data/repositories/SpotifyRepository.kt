package com.yanfiq.streamfusion.data.repositories

import android.content.Context
import com.yanfiq.streamfusion.domain.model.Track

interface SpotifyRepository {
    suspend fun search(query: String, limit: Int, context: Context, clientId: String, clientSecret: String, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit)
}