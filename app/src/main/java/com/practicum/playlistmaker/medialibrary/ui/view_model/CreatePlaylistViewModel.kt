package com.practicum.playlistmaker.medialibrary.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow("")
    val stateFlow: StateFlow<String> = _stateFlow.asStateFlow()

    fun createPlaylist(cover: String, title: String, description: String) {
        val playlist = PlaylistEntity(null, title, description, cover, null)
        viewModelScope.launch {
            playlistInteractor.add(playlist)
            _stateFlow.value = title
        }
    }
}