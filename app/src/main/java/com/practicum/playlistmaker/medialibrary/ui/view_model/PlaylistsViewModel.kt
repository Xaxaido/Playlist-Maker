package com.practicum.playlistmaker.medialibrary.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.MediaLibraryState
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<MediaLibraryState>(MediaLibraryState.Loading)
    val stateFlow: StateFlow<MediaLibraryState> = _stateFlow.asStateFlow()

    init {
        observePlaylists()
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            playlistsInteractor
                .getAll()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        setState(
            when {
                playlists.isNotEmpty() -> MediaLibraryState.Content(playlists)
                else -> MediaLibraryState.Empty
            }
        )
    }

    private fun setState(state: MediaLibraryState) {
        _stateFlow.value = state
    }
}