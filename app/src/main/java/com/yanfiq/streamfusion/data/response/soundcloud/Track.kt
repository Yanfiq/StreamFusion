package com.yanfiq.youcloudify.data.response.soundcloud

data class Track(
    val id: Int,
    val title: String,
    val user: User,
    val artwork_url: String?,
    val stream_url: String
)
