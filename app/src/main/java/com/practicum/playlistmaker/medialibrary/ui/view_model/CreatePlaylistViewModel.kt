package com.practicum.playlistmaker.medialibrary.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.practicum.playlistmaker.common.resources.CreatePlaylistState
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    gson: Gson,
    json: String?,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<CreatePlaylistState>(CreatePlaylistState.Default)
    val stateFlow: StateFlow<CreatePlaylistState> = _stateFlow.asStateFlow()

    init {
        gson.fromJson(json, Playlist::class.java)?.let {
            setState(CreatePlaylistState.Edit(it))
        }
    }

    fun createPlaylist(id: Int? = null, cover: String, title: String, description: String, tracksCount: Int? = null) {
        val playlist = PlaylistEntity(id, title, description, cover, tracksCount ?: 0)
        viewModelScope.launch {
            playlistsInteractor.add(playlist)
            setState(CreatePlaylistState.Create(title))
        }
    }

    fun saveImage(uri: String): String {
        return playlistsInteractor.saveImage(uri)
    }

    private fun setState(state: CreatePlaylistState) {
        _stateFlow.value = state
    }
}