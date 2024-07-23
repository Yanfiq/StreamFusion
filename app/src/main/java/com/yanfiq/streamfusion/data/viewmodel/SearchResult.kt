package com.yanfiq.streamfusion.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanfiq.streamfusion.domain.model.audius.Track
import com.yanfiq.streamfusion.domain.model.youtube.Video

class SearchResult : ViewModel() {
    private val _audiusSearchData = MutableLiveData<List<Track>>()
    private val _soundcloudSearchData = MutableLiveData<List<com.yanfiq.streamfusion.domain.model.soundcloud.Track>>()
    private val _spotifySearchData = MutableLiveData<List<com.yanfiq.streamfusion.domain.model.spotify.Track>>()
    private val _youtubeSearchData = MutableLiveData<List<Video>>()

    val audiusSearchData: LiveData<List<Track>> get() = _audiusSearchData
    val soundcloudSearchData: LiveData<List<com.yanfiq.streamfusion.domain.model.soundcloud.Track>> get() = _soundcloudSearchData
    val spotifySearchData: LiveData<List<com.yanfiq.streamfusion.domain.model.spotify.Track>> get() = _spotifySearchData
    val youtubeSearchData: LiveData<List<Video>> get() = _youtubeSearchData

    fun updateAudiusSearchData(newData: List<Track>) {
        _audiusSearchData.postValue(newData)
    }

    fun updateSoundcloudSearchData(newData: List<com.yanfiq.streamfusion.domain.model.soundcloud.Track>) {
        _soundcloudSearchData.postValue(newData)
    }

    fun updateSpotifySearchData(newData: List<com.yanfiq.streamfusion.domain.model.spotify.Track>){
        _spotifySearchData.postValue(newData)
    }

    fun updateYoutubeSearchData(newData: List<Video>){
        _youtubeSearchData.postValue(newData)
    }
}