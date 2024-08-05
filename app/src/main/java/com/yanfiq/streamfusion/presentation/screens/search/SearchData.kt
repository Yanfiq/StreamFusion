package com.yanfiq.streamfusion.presentation.screens.search

data class SearchData(
    var result: List<com.yanfiq.streamfusion.domain.model.Track> = emptyList(),
    var isLoading: Boolean = false,
    var message: String = ""
)
