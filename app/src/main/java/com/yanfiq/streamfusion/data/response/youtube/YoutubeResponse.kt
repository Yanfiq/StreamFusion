package com.yanfiq.streamfusion.data.response.youtube

import com.yanfiq.streamfusion.domain.model.youtube.VideoItem

data class YouTubeResponse(
    val items: List<VideoItem>
)