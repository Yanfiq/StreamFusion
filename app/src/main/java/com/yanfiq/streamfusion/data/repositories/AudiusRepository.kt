package com.yanfiq.streamfusion.data.repositories

import android.content.Context
import com.yanfiq.streamfusion.domain.model.Track
import com.yanfiq.streamfusion.presentation.viewmodels.ApiStatus

interface AudiusRepository {
    suspend fun search(query: String, limit: Int, context: Context, apiStatus: ApiStatus, onResults: (List<Track>) -> Unit)
}
