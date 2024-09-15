package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.common.resources.InternetState
import com.practicum.playlistmaker.main.domain.api.InternetConnectionCallback
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor

class MainActivityModel(
    private val internetConnectionInteractor: InternetConnectionInteractor,
) : ViewModel(), InternetConnectionCallback {

    private val _liveData = MutableLiveData<InternetState>()
    val liveData: LiveData<InternetState> = _liveData

    init {
        internetConnectionInteractor.setCallback(this)
        internetConnectionInteractor.register()
    }

    private fun setState(state: InternetState) { _liveData.postValue(state) }

    override fun onConnected() {
        setState(InternetState.Connected)
    }

    override fun onDisconnected() {
        setState(InternetState.ConnectionLost)
    }

    override fun onCleared() {
        internetConnectionInteractor.unregister()
    }
}