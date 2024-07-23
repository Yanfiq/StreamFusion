package com.yanfiq.streamfusion.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchStatus : ViewModel() {
    private val _audiusSearchStatus = MutableLiveData<Boolean>()
    private val _soundcloudSearchStatus = MutableLiveData<Boolean>()
    private val _spotifySearchStatus = MutableLiveData<Boolean>()
    private val _youtubeSearchStatus = MutableLiveData<Boolean>()

    val audiusSearchStatus: LiveData<Boolean> get() = _audiusSearchStatus
    val soundcloudSearchStatus: LiveData<Boolean> get() = _soundcloudSearchStatus
    val spotifySearchStatus: LiveData<Boolean> get() = _spotifySearchStatus
    val youtubeSearchStatus: LiveData<Boolean> get() = _youtubeSearchStatus

    fun updateAudiusSearchStatus(newData: Boolean){
        _audiusSearchStatus.postValue(newData)
    }

    fun updateSoundcloudSearchStatus(newData: Boolean){
        _soundcloudSearchStatus.postValue(newData)
    }

    fun updateSpotifySearchStatus(newData: Boolean){
        _spotifySearchStatus.postValue(newData)
    }

    fun updateYoutubeSearchStatus(newData: Boolean){
        _youtubeSearchStatus.postValue(newData)
    }

    private val _pendingSearchQuery = MutableLiveData<String?>()
    val pendingSearchQuery: LiveData<String?> get() = _pendingSearchQuery
    fun setPendingSearchQuery(query: String?) {
        _pendingSearchQuery.postValue(query)
    }
}