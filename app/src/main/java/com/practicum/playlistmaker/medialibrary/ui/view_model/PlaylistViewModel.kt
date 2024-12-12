package com.practicum.playlistmaker.medialibrary.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.PlaylistState
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    playlistId: Int,
) : ViewModel() {

    private val _playlistFlow = MutableStateFlow<PlaylistState>(PlaylistState.Default)
    val playlistFlow: StateFlow<PlaylistState> = _playlistFlow.asStateFlow()

    private val _tracksLIstFlow = MutableStateFlow<List<Track>>(emptyList())
    val tracksLIstFlow: StateFlow<List<Track>> = _tracksLIstFlow.asStateFlow()

    private lateinit var playlist: Playlist

    init {
        observePlaylist(playlistId)
    }

    fun removeTrack(trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrack(playlist, trackId)
        }
    }

    private fun observePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylist(playlistId)
                .collect {
                    playlist = it
                    processResult(it)
                }
        }
    }

    private fun processResult(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor
                .getTracks(playlist.tracks)
                .collect { tracks ->
                    var duration = 0L

                    tracks.forEach { duration += it.duration }
                    _playlistFlow.value = PlaylistState.PlaylistInfo(playlist, duration)
                    _tracksLIstFlow.value = tracks
                }
        }
    }
}