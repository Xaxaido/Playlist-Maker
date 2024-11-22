package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.player.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val trackDescriptionInteractor: TrackDescriptionInteractor,
    private val playerInteractor: PlayerInteractor,
    json: String,
) : ViewModel(), MediaPlayerListener {

    private val track: Track = Util.jsonToTrack(json)
    private val timers: Map<String, Debounce> = mapOf(
        UPDATE_PLAYBACK_PROGRESS to Debounce(Util.UPDATE_PLAYBACK_PROGRESS_DELAY, viewModelScope) {
            setState(PlayerState.CurrentTime(playerInteractor.currentPosition.millisToSeconds()))
        },
        UPDATE_BUFFERED_PROGRESS to Debounce(100, viewModelScope) {
            setState(PlayerState.BufferedProgress(playerInteractor.bufferedProgress))
        },
    )

    private val _stateFlow = MutableStateFlow<PlayerState>(PlayerState.Default)
    val stateFlow: StateFlow<PlayerState> = _stateFlow.asStateFlow()

    init {
        playerInteractor.init(this, track)
        setState(PlayerState.TrackData(track))
        Handler(Looper.getMainLooper()).postDelayed({
            setState(PlayerState.IsFavorite(track.isFavorite, false))
        }, 100)
    }

    fun addToPlaylist() {
        setState(PlayerState.IsPlayListed(track.isFavorite))
    }

    fun addToFavorites() {
        favoriteTracksInteractor.addToFavorites(viewModelScope, track) {
            setState(PlayerState.IsFavorite(track.isFavorite))
        }
    }

    fun controlPlayback(shouldPlay: Boolean = true) {
        playerInteractor.apply {
            if (isPlaying) {
                setState(PlayerState.IsPlaying(false))
                pause()
            } else if (shouldPlay) {
                setState(PlayerState.IsPlaying(true))
                play()
            }
        }

        updatePlaybackProgressTimerState()
    }

    private fun setState(state: PlayerState) { _stateFlow.value = state }

    fun searchTrackDescription(url: String?) {
        url?.let{
            viewModelScope.launch {
                trackDescriptionInteractor.searchTrackDescription(it)
                    .collect { result ->
                        setState(PlayerState.Description(result))
                    }
            }
        } ?: setState(PlayerState.Description(TrackDescription(null)))
    }

    private fun updatePlaybackProgressTimerState() {
        (timers[UPDATE_PLAYBACK_PROGRESS] as Debounce).apply {
            if (isRunning) stop() else start(true)
        }
    }

    private fun release() {
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

    override fun onCleared() {
        release()
        super.onCleared()
    }

    private companion object {
        const val UPDATE_PLAYBACK_PROGRESS = "UPDATE_PLAYBACK_PROGRESS"
        const val UPDATE_BUFFERED_PROGRESS = "UPDATE_BUFFERED_PROGRESS"
    }
}