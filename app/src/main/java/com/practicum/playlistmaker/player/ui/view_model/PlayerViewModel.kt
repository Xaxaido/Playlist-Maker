package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Util.millisToSeconds
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.search.domain.api.TrackDescriptionInteractor

class PlayerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val consumer = object : TrackDescriptionInteractor.TracksDescriptionConsumer {

        override fun consume(result: TrackDescription) {
            setState(PlayerState.Description(result))
        }
    }
    private val trackDescriptionInteractor = Creator.getTrackDescriptionInteractor(getApplication())
    private val playerInteractor = Creator.getPlayerInteractor(getApplication())
    private val timer: Debounce by lazy {
        Debounce { updateProgress() }
    }
    private val _liveData = MutableLiveData<PlayerState>()
    val liveData: LiveData<PlayerState> get() = _liveData

    private fun updateTimer(isPlaying: Boolean) {
        if (!isPlaying && timer.isRunning) {
            timer.stop()
        } else if (isPlaying) {
            timer.start(true)
        }
    }

    fun init(stateListener: MediaPlayerListener, track: Track) {
        playerInteractor.init(stateListener, track)
    }

    fun updateProgress() {
        if (playerInteractor.isPlaying) {
            setState(PlayerState.CurrentTime(playerInteractor.currentPosition.millisToSeconds()))
        } else {
            setState(PlayerState.Stop)
        }
    }

    fun controlPlayback() {
        var isPaused: Boolean
        playerInteractor.apply {
            if (isPlaying) {
                isPaused = true
                pause()
            } else {
                isPaused = false
                play()
            }
        }

        updateTimer(!isPaused)
        setState(if (isPaused) PlayerState.Paused else PlayerState.Playing)
    }

    private fun setState(state: PlayerState) {
        _liveData.postValue(state)
    }

    fun release() {
        updateTimer(playerInteractor.isPlaying)
        playerInteractor.release()
    }

    fun searchTrackDescription(url: String) {
        trackDescriptionInteractor.searchTrackDescription(url, consumer)
    }

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    this[APPLICATION_KEY] as Application
                )
            }
        }
    }
}
