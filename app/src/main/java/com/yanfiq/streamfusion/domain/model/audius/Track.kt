package com.yanfiq.streamfusion.domain.model.audius

data class Track(
    val id: String,
    val artwork: Artwork,
    val title: String,
    val user: Artist
)
