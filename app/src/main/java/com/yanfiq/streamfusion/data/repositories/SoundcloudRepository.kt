package com.yanfiq.streamfusion.data.repositories

import android.content.Context
import com.yanfiq.streamfusion.domain.model.Track

interface SoundcloudRepository {
    suspend fun search(query: String, limit: Int, context: Context, onProgress: (String) -> Unit, onResults: (List<Track>) -> Unit)
}