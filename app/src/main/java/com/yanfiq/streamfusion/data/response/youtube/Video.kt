package com.yanfiq.streamfusion.data.response.youtube

data class Video(val id: String, val title: String, val description: String, val thumbnailUrl: String, var duration: String = "")
