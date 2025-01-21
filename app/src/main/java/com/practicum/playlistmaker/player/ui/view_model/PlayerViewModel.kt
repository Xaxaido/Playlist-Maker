package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.player.services.AudioPlayerControl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val trackDescriptionInteractor: TrackDescriptionInteractor,
    json: String,
) : ViewModel() {

    val track: Track = Util.jsonToTrack(json)

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _trackBufferingState = MutableStateFlow(0)
    val trackBufferingState: StateFlow<Int> = _trackBufferingState.asStateFlow()

    private var audioPlayerControl: AudioPlayerControl? = null

    init {
        isTrackFavorite(track.id)
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.playerState.collect { state ->
                if (state is PlayerState.CurrentTime && _playerState.value is PlayerState.Stop) return@collect
                _playerState.value = state
            }
        }

        viewModelScope.launch {
            audioPlayerControl.trackBufferingState.collect { state ->
                _trackBufferingState.value = state
            }
        }
    }

    fun setTrack() {
        setState(PlayerState.TrackData(track))
    }

    fun addToFavorites() {
        favoriteTracksInteractor.addToFavorites(viewModelScope, track)
    }

    fun searchTrackDescription(url: String?) {
        url?.let{
            viewModelScope.launch {
                trackDescriptionInteractor
                    .searchTrackDescription(it)
                    .collect { result ->
                        setState(PlayerState.Description(result))
                    }
            }
        } ?: setState(PlayerState.Description(TrackDescription(null)))
    }

    fun showNotification(shouldShow: Boolean) {
        if (!shouldShow) {
            audioPlayerControl?.stopForeground()
        } else if (audioPlayerControl?.isPLaying == true) {
            audioPlayerControl?.startForeground()
        }
    }

    fun playbackControl() {
        if (audioPlayerControl?.isPLaying == true) {
            audioPlayerControl?.pause()
        } else {
            audioPlayerControl?.play()
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    private fun setState(state: PlayerState) { _playerState.value = state }

    private fun isTrackFavorite(id: Long) {
        viewModelScope.launch {
            favoriteTracksInteractor
                .isTrackFavorite(id)
                .collect { isFavorite ->
                    setState(PlayerState.IsFavorite(isFavorite))
                }
        }
    }

    override fun onCleared() {
        removeAudioPlayerControl()
        super.onCleared()
    }
}