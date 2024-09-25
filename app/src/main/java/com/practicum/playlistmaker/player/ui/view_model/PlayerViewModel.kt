package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.player.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.player.domain.api.TracksDescriptionConsumer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val trackDescriptionInteractor: TrackDescriptionInteractor,
    private val playerInteractor: PlayerInteractor,
) : ViewModel(), MediaPlayerListener {

    private val consumer = object : TracksDescriptionConsumer {

        override fun consume(result: TrackDescription) {
            setState(PlayerState.Description(result))
        }
    }
    private val timers: Map<String, Debounce> = mapOf(
        UPDATE_PLAYBACK_PROGRESS to Debounce { updateProgress() },
        UPDATE_BUFFERED_PROGRESS to Debounce(100) {
            setState(PlayerState.BufferedProgress(playerInteractor.bufferedProgress))
        },
    )
    private val _liveData = MutableLiveData<PlayerState>()
    val liveData: LiveData<PlayerState> get() = _liveData

    fun getTrack(json: String) = playerInteractor.jsonToTrack(json)

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

    fun searchTrackDescription(url: String?) {
        url?.let{
            trackDescriptionInteractor.searchTrackDescription(it, consumer)
        } ?: setState(PlayerState.Description(TrackDescription(null)))
    }

    private fun updatePlaybackProgressTimerState() {
        (timers[UPDATE_PLAYBACK_PROGRESS] as Debounce).apply {
            if (isRunning) stop() else start(true)
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
                (timers[UPDATE_BUFFERED_PROGRESS] as Debounce).stop()
                setState(PlayerState.Ready)
            }
            Player.STATE_ENDED -> setState(PlayerState.Stop)
            Player.STATE_BUFFERING -> {
                (timers[UPDATE_BUFFERED_PROGRESS] as Debounce).start(true)
            }
            else -> {}
        }
    }

    private companion object {
        const val UPDATE_PLAYBACK_PROGRESS = "UPDATE_PLAYBACK_PROGRESS"
        const val UPDATE_BUFFERED_PROGRESS = "UPDATE_BUFFERED_PROGRESS"
    }
}
