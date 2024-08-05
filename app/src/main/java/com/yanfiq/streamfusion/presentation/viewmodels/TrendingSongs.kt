package com.yanfiq.streamfusion.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrendingSongs: ViewModel() {
    private val _audiusTrends = MutableLiveData<List<com.yanfiq.streamfusion.domain.model.audius.Track>>()

    val audiusTrends: LiveData<List<com.yanfiq.streamfusion.domain.model.audius.Track>> get() = _audiusTrends

    fun updateAudiusTrends(newData: List<com.yanfiq.streamfusion.domain.model.audius.Track>){
        _audiusTrends.postValue(newData)
    }

}