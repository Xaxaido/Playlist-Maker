package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.PlaylistsBottomDialogFragmentState
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsBottomSheetDialogViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val track: Track,
) : ViewModel() {

    private val _playlistsFlow = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistsFlow: StateFlow<List<Playlist>> = _playlistsFlow.asStateFlow()

    private val _addToPlaylistFlow = MutableStateFlow<PlaylistsBottomDialogFragmentState>(PlaylistsBottomDialogFragmentState.Default)
    val addToPlaylistFlow: StateFlow<PlaylistsBottomDialogFragmentState> = _addToPlaylistFlow.asStateFlow()

    init {
        observePlaylists()
    }

    fun addToPlaylist(playlist: Playlist) {
        val tracks: List<Long> = playlistsInteractor.getTracks(playlist.tracks)

        if (tracks.contains(track.id)) {
            _addToPlaylistFlow.value = PlaylistsBottomDialogFragmentState.AddToPlaylist(playlist.name, false)
        } else {
            viewModelScope.launch {
                playlistsInteractor.addToPlaylist(playlist, track)
                _addToPlaylistFlow.value = PlaylistsBottomDialogFragmentState.AddToPlaylist(playlist.name, true)
            }
        }
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAll()
                .collect { playlists ->
                    _playlistsFlow.value = playlists
                }
        }
    }
}