package com.yanfiq.streamfusion.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanfiq.streamfusion.domain.model.Track

class SearchResult : ViewModel() {
    private val _audiusSearchData = MutableLiveData<List<Track>>()
    private val _soundcloudSearchData = MutableLiveData<List<Track>>()
    private val _spotifySearchData = MutableLiveData<List<Track>>()
    private val _youtubeSearchData = MutableLiveData<List<Track>>()

    val audiusSearchData: LiveData<List<com.yanfiq.streamfusion.domain.model.Track>> get() = _audiusSearchData
    val soundcloudSearchData: LiveData<List<Track>> get() = _soundcloudSearchData
    val spotifySearchData: LiveData<List<Track>> get() = _spotifySearchData
    val youtubeSearchData: LiveData<List<Track>> get() = _youtubeSearchData

    fun updateAudiusSearchData(newData: List<Track>) {
        _audiusSearchData.postValue(newData)
    }

    fun updateSoundcloudSearchData(newData: List<Track>) {
        _soundcloudSearchData.postValue(newData)
    }

    fun updateSpotifySearchData(newData: List<Track>){
        _spotifySearchData.postValue(newData)
    }

    fun updateYoutubeSearchData(newData: List<Track>){
        _youtubeSearchData.postValue(newData)
    }
}