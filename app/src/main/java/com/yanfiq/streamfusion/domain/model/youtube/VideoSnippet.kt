package com.yanfiq.streamfusion.domain.model.youtube

data class VideoSnippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String
)