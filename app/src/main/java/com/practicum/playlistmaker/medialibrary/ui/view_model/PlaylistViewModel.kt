package com.practicum.playlistmaker.medialibrary.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.PlaylistMenuState
import com.practicum.playlistmaker.common.resources.PlaylistState
import com.practicum.playlistmaker.common.utils.DtoConverter.toPlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    playlistId: Int,
) : ViewModel() {

    private val _playlistFlow = MutableStateFlow<PlaylistState>(PlaylistState.Default)
    val playlistFlow: StateFlow<PlaylistState> = _playlistFlow.asStateFlow()

    private val _tracksListFlow = MutableStateFlow<List<Track>>(emptyList())
    val tracksListFlow: StateFlow<List<Track>> = _tracksListFlow.asStateFlow()

    private val _playlistMenuFlow = MutableSharedFlow<PlaylistMenuState>()
    val playlistMenuFlow: SharedFlow<PlaylistMenuState> = _playlistMenuFlow

    private lateinit var _playlist: Playlist
    val playlist: Playlist get() = _playlist
    private lateinit var tracksList: List<Track>

    init {
        observePlaylist(playlistId)
    }

    fun sharePlaylist() {
        setMenuState(PlaylistMenuState.Share(_playlist, tracksList))
    }

    fun removeTrack(trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrack(_playlist, trackId)
        }
    }

    fun removePlaylist() {
        viewModelScope.launch {
            playlistInteractor.removePlaylist(_playlist.toPlaylistEntity())
            setMenuState(PlaylistMenuState.Remove)
        }
    }

    private fun setMenuState(state: PlaylistMenuState) {
        viewModelScope.launch {
            _playlistMenuFlow.emit(state)
        }
    }

    private fun observePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylist(playlistId)
                .collect {
                    _playlist = it
                    processResult(it)
                }
        }
    }

    private fun processResult(playlist: Playlist?) {
        viewModelScope.launch {
            playlistInteractor
                .getTracks(playlist?.tracks)
                .collect { tracks ->
                    if (playlist == null) return@collect

                    tracksList = tracks
                    var duration = 0L

                    tracks.forEach { duration += it.duration }
                    _playlistFlow.value = PlaylistState.PlaylistInfo(playlist, duration)
                    _tracksListFlow.value = tracks
                }
        }
    }
}