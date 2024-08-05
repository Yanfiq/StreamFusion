package com.yanfiq.streamfusion.domain.model

import kotlin.time.Duration

data class Track(
    val trackId: String,
    val tractTitle: String,
    val trackArtist: String,
    val durationInSeconds: Int,
    val trackArtworkUrl: String
)
