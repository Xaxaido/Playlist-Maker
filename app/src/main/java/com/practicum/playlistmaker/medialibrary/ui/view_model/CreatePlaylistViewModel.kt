package com.practicum.playlistmaker.medialibrary.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.practicum.playlistmaker.common.resources.CreatePlaylistState
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val gson: Gson,
    private val json: String?,
) : ViewModel() {

    private val _sharedFlow = MutableSharedFlow<CreatePlaylistState>()
    val sharedFlow: SharedFlow<CreatePlaylistState> = _sharedFlow

    fun isEditMode() {
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
        viewModelScope.launch {
            _sharedFlow.emit(state)
        }
    }
}