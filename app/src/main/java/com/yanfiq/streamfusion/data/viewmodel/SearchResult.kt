package com.yanfiq.streamfusion.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchResult : ViewModel() {
    private val _soundcloudSearchData = MutableLiveData<List<com.yanfiq.streamfusion.data.response.soundcloud.Track>>()
    private val _audiusSearchData = MutableLiveData<List<com.yanfiq.streamfusion.data.response.audius.Track>>()
    private val _youtubeSearchData = MutableLiveData<List<com.yanfiq.streamfusion.data.response.youtube.Video>>()

    val soundcloudSearchData: LiveData<List<com.yanfiq.streamfusion.data.response.soundcloud.Track>> get() = _soundcloudSearchData
    val audiusSearchData: LiveData<List<com.yanfiq.streamfusion.data.response.audius.Track>> get() = _audiusSearchData
    val youtubeSearchData: LiveData<List<com.yanfiq.streamfusion.data.response.youtube.Video>> get() = _youtubeSearchData

    fun updateSoundcloudSearchData(newData: List<com.yanfiq.streamfusion.data.response.soundcloud.Track>) {
        _soundcloudSearchData.postValue(newData)
    }

    fun updateAudiusSearchData(newData: List<com.yanfiq.streamfusion.data.response.audius.Track>) {
        _audiusSearchData.postValue(newData)
    }

    fun updateYoutubeSearchData(newData: List<com.yanfiq.streamfusion.data.response.youtube.Video>){
        _youtubeSearchData.postValue(newData)
    }
}