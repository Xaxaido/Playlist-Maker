package com.practicum.playlistmaker.player.ui.view_model

import android.graphics.Bitmap
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

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _trackDescription = MutableStateFlow(TrackDescription(null))
    val trackDescription: StateFlow<TrackDescription> = _trackDescription.asStateFlow()

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

    fun showNotification(shouldShow: Boolean, cover: Bitmap? = null) {
        if (!shouldShow) {
            audioPlayerControl?.stopForeground()
        } else if (audioPlayerControl?.isPLaying == true) {
            audioPlayerControl?.startForeground(cover)
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
                .collect { favorite ->
                    _isFavorite.value = favorite
                }
        }
    }

    fun searchTrackDescription(url: String?) {
        if (url != null) {
            viewModelScope.launch {
                trackDescriptionInteractor
                    .searchTrackDescription(url)
                    .collect { result ->
                        _trackDescription.value = result
                    }
            }
        } else {
            _trackDescription.value = TrackDescription(null)
        }
    }

    override fun onCleared() {
        removeAudioPlayerControl()
        super.onCleared()
    }
}