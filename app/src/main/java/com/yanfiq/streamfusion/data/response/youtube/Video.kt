package com.yanfiq.streamfusion.data.response.youtube

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channel: String = "Youtube User",
    var duration: String = ""
)
