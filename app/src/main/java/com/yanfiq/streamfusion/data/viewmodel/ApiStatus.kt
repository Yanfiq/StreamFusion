package com.yanfiq.streamfusion.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ApiStatus : ViewModel() {
    private val _audiusApiReady = MutableLiveData<Boolean>()
    private val _spotifyApiReady = MutableLiveData<Boolean>()

    val audiusApiReady: LiveData<Boolean> get() = _audiusApiReady
    val spotifyApiReady: LiveData<Boolean> get() = _spotifyApiReady

    fun updateAudiusApiReady(newValue: Boolean){
        _audiusApiReady.postValue(newValue)
    }

    fun updateSpotifyApiReady(newValue: Boolean){
        _spotifyApiReady.postValue(newValue)
    }
}