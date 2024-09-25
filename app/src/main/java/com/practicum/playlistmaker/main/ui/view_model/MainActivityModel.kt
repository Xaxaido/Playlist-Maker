package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.common.resources.InternetState
import com.practicum.playlistmaker.main.domain.api.InternetConnectListener
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor

class MainActivityModel(
    private val internetConnectionInteractor: InternetConnectionInteractor,
) : ViewModel(), InternetConnectListener {

    private val _liveData = MutableLiveData<InternetState>()
    val liveData: LiveData<InternetState> = _liveData

    init {
        internetConnectionInteractor.addOnInternetConnectListener(this)
        internetConnectionInteractor.register()
    }

    private fun setState(state: InternetState) { _liveData.postValue(state) }

    override fun onConnectionStatusUpdate(hasInternet: Boolean) {
        setState(
            if (hasInternet) InternetState.Connected
            else InternetState.ConnectionLost
        )
    }

    override fun onCleared() {
        internetConnectionInteractor.removeOnInternetConnectListener(this)
        internetConnectionInteractor.unregister()
    }
}