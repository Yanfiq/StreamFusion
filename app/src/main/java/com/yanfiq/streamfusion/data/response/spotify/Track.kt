package com.yanfiq.streamfusion.data.response.spotify

data class Track(val id: String, val name: String, val artists: List<Artist>, val album: Album)
