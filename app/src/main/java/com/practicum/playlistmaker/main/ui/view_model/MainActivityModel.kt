package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.common.resources.InternetState
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor

class MainActivityModel(
    private val internetConnectionInteractor: InternetConnectionInteractor,
) : ViewModel() {

    private val internetStatusObserver = Observer<Boolean> { connected ->
        setState(
            if (connected) InternetState.Connected
            else InternetState.ConnectionLost
        )
    }
    private val _liveData = MutableLiveData<InternetState>()
    val liveData: LiveData<InternetState> = _liveData

    init {
        internetConnectionInteractor.register()
        internetConnectionInteractor.internetStatus.observeForever(internetStatusObserver)
    }

    private fun setState(state: InternetState) { _liveData.postValue(state) }

    override fun onCleared() {
        internetConnectionInteractor.unregister()
        internetConnectionInteractor.internetStatus.removeObserver(internetStatusObserver)
    }
}