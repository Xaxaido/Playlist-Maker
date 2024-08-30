package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.Player
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Util.millisToSeconds
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Util.UPDATE_BUFFERED_PROGRESS
import com.practicum.playlistmaker.common.utils.Util.UPDATE_PLAYBACK_PROGRESS
import com.practicum.playlistmaker.player.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.search.domain.api.TrackDescriptionInteractor

class PlayerViewModel(
    application: Application
) : AndroidViewModel(application), MediaPlayerListener {

    private val consumer = object : TrackDescriptionInteractor.TracksDescriptionConsumer {

        override fun consume(result: TrackDescription) {
            setState(PlayerState.Description(result))
        }
    }
    private val trackDescriptionInteractor = Creator.getTrackDescriptionInteractor(getApplication())
    private val playerInteractor = Creator.getPlayerInteractor(getApplication())
    private val timers: Map<String, Debounce> = mapOf(
        UPDATE_PLAYBACK_PROGRESS to Debounce { updateProgress() },
        UPDATE_BUFFERED_PROGRESS to Debounce(100) {
            setState(PlayerState.BufferedProgress(playerInteractor.bufferedProgress))
        },
    )
    private val _liveData = MutableLiveData<PlayerState>()
    val liveData: LiveData<PlayerState> get() = _liveData

    fun init(track: Track) {
        setState(PlayerState.Stop)
        playerInteractor.init(this, track)
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

        updatePlaybackProgressTimerState()
        setState(if (isPaused) PlayerState.Paused else PlayerState.Playing)
    }

    private fun setState(state: PlayerState) {
        _liveData.postValue(state)
    }

    fun searchTrackDescription(url: String) {
        trackDescriptionInteractor.searchTrackDescription(url, consumer)
    }

    private fun updatePlaybackProgressTimerState() {
        val timer = timers[UPDATE_PLAYBACK_PROGRESS]!!

        if (timer.isRunning) {
            timer.stop()
        } else {
            timer.start(true)
        }
    }

    private fun updateProgress() {
        if (playerInteractor.isPlaying) {
            setState(PlayerState.CurrentTime(playerInteractor.currentPosition.millisToSeconds()))
        } else {
            setState(PlayerState.Stop)
        }
    }

    fun release() {
        timers.forEach {
            if (it.value.isRunning) it.value.stop()
        }
        playerInteractor.release()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                timers[UPDATE_BUFFERED_PROGRESS]!!.stop()
                setState(PlayerState.Ready)
            }
            Player.STATE_ENDED -> setState(PlayerState.Stop)
            Player.STATE_BUFFERING -> {
                timers[UPDATE_BUFFERED_PROGRESS]!!.start(true)
            }
            else -> {}
        }
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
