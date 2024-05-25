package com.yanfiq.streamfusion.data.response.audius

import com.google.gson.annotations.SerializedName

data class Artwork(
    @SerializedName("150x150") val small: String,
    @SerializedName("480x480") val medium: String,
    @SerializedName("1000x1000") val large: String
)
