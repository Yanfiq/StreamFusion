package com.yanfiq.streamfusion.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchStatus : ViewModel() {
    private val _soundcloudSearchStatus = MutableLiveData<Boolean>()
    private val _audiusSearchStatus = MutableLiveData<Boolean>()

    val soundcloudSearchStatus: LiveData<Boolean> get() = _soundcloudSearchStatus
    val audiusSearchStatus: LiveData<Boolean> get() = _audiusSearchStatus

    fun updateSoundcloudSearchStatus(newData: Boolean){
        _soundcloudSearchStatus.postValue(newData)
    }

    fun updateAudiusSearchStatus(newData: Boolean){
        _audiusSearchStatus.postValue(newData)
    }
}