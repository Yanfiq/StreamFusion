package com.yanfiq.streamfusion.domain.model.soundcloud

data class Track(
    val title: String,
    val user: String,
    val artwork_url: String?,
    val stream_url: String,
    val duration: Int
)
