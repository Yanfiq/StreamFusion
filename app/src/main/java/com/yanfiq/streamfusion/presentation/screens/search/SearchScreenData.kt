package com.yanfiq.streamfusion.presentation.screens.search

data class SearchScreenData(
    val audiusSearchData: SearchData = SearchData(),
    val soundcloudSearchData: SearchData = SearchData(),
    val spotifySearchData: SearchData = SearchData(),
    val youtubeSearchData: SearchData = SearchData()
)
