package com.practicum.playlistmaker.player.ui.view_model

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

    val track: Track = Util.jsonToTrack(json)
    private var shouldPlayAnimation = false
    private val timers: Map<String, Debounce<Any>> = mapOf(
        UPDATE_PLAYBACK_PROGRESS to Debounce(Util.UPDATE_PLAYBACK_PROGRESS_DELAY, viewModelScope) {
            setState(PlayerState.CurrentTime(playerInteractor.currentPosition.millisToSeconds()))
        },
        UPDATE_BUFFERED_PROGRESS to Debounce(100, viewModelScope) {
            _trackBufferingFlow.value = playerInteractor.bufferedProgress
        },
    )

    private val _stateFlow = MutableStateFlow<PlayerState>(PlayerState.Default)
    val stateFlow: StateFlow<PlayerState> = _stateFlow.asStateFlow()

    private val _trackBufferingFlow = MutableStateFlow(0)
    val trackBufferingFlow: StateFlow<Int> = _trackBufferingFlow.asStateFlow()

    init {
        playerInteractor.init(this, track)
        observeFavoriteTracks()
    }

    fun setTrack() {
        setState(PlayerState.TrackData(track))
    }

    fun addToFavorites() {
        shouldPlayAnimation = true
        favoriteTracksInteractor.addToFavorites(viewModelScope, track)
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

    private fun setState(state: PlayerState) { _stateFlow.value = state }

    private fun observeFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getIds()
                .collect { ids ->
                    val isFavorite = ids.contains(track.id)
                    setState(PlayerState.IsFavorite(isFavorite, shouldPlayAnimation))
            }
        }
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
        _trackBufferingFlow.value = 100
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