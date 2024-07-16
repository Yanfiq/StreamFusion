package com.yanfiq.streamfusion.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchResult : ViewModel() {
    private val _soundcloudSearchData = MutableLiveData<List<com.yanfiq.streamfusion.data.response.soundcloud.Track>>()
    private val _audiusSearchData = MutableLiveData<List<com.yanfiq.streamfusion.data.response.audius.Track>>()

    val soundcloudSearchData: LiveData<List<com.yanfiq.streamfusion.data.response.soundcloud.Track>> get() = _soundcloudSearchData
    val audiusSearchData: LiveData<List<com.yanfiq.streamfusion.data.response.audius.Track>> get() = _audiusSearchData

    fun updateSoundcloudSearchData(newData: List<com.yanfiq.streamfusion.data.response.soundcloud.Track>) {
        _soundcloudSearchData.postValue(newData)
    }

    fun updateAudiusSearchData(newData: List<com.yanfiq.streamfusion.data.response.audius.Track>) {
        _audiusSearchData.postValue(newData)
    }
}