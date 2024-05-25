package com.yanfiq.streamfusion.data.response.audius

data class Track(
    val id: String,
    val artwork: Artwork,
    val title: String,
    val user: Artist,
    val stream_url: String
)
