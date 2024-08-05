package com.yanfiq.streamfusion.domain.model.spotify

data class Track(
    val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val duration_ms: Int
)
