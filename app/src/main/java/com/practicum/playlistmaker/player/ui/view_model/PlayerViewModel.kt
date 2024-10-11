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

class PlayerViewModel(
    private val trackDescriptionInteractor: TrackDescriptionInteractor,
    private val playerInteractor: PlayerInteractor,
) : ViewModel(), MediaPlayerListener {

    private val consumer = object : TracksDescriptionConsumer {

        override fun consume(result: TrackDescription) {
            setState(PlayerState.Description(result))
        }
    }
    private val timers: Map<String, Debounce> = mapOf(
        UPDATE_PLAYBACK_PROGRESS to Debounce {
            setState(PlayerState.CurrentTime(playerInteractor.currentPosition.millisToSeconds()))
        },
        UPDATE_BUFFERED_PROGRESS to Debounce(100) {
            setState(PlayerState.BufferedProgress(playerInteractor.bufferedProgress))
        },
    )
    private val _liveData = MutableLiveData<PlayerState>()
    val liveData: LiveData<PlayerState> get() = _liveData

    fun getTrack(json: String) = playerInteractor.jsonToTrack(json)

    fun init(track: Track) {
        playerInteractor.init(this, track)
    }

    fun controlPlayback() {
        playerInteractor.apply {
            if (isPlaying) {
                setState(PlayerState.Paused)
                pause()
            } else {
                setState(PlayerState.Playing)
                play()
            }
        }

        updatePlaybackProgressTimerState()
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

    fun release() {
        timers.forEach {
            if (it.value.isRunning) it.value.stop()
        }
        playerInteractor.release()
    }

    private fun ready() {
        setState(PlayerState.Ready)
        (timers[UPDATE_BUFFERED_PROGRESS] as Debounce).stop()
    }

    private fun stop() {
        setState(PlayerState.Stop)
        updatePlaybackProgressTimerState()
    }

    private fun loadTrack() {
        (timers[UPDATE_BUFFERED_PROGRESS] as Debounce).start(true)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> ready()
            Player.STATE_ENDED -> stop()
            Player.STATE_BUFFERING -> loadTrack()
            else -> {}
        }
    }

    private companion object {
        const val UPDATE_PLAYBACK_PROGRESS = "UPDATE_PLAYBACK_PROGRESS"
        const val UPDATE_BUFFERED_PROGRESS = "UPDATE_BUFFERED_PROGRESS"
    }
}
