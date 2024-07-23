package com.yanfiq.streamfusion.domain.model.youtube

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channel: String = "Youtube User",
    var duration: String = ""
)
