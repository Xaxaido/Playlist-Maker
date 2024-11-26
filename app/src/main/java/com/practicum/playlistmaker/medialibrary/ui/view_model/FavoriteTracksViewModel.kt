package com.practicum.playlistmaker.medialibrary.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.FavoriteTracksState
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<FavoriteTracksState>(FavoriteTracksState.Loading)
    val stateFlow: StateFlow<FavoriteTracksState> = _stateFlow.asStateFlow()

    init {
        observeFavoriteTracks()
    }

    fun addToFavorites(track: Track) {
        favoriteTracksInteractor.addToFavorites(viewModelScope, track)
    }

    private fun observeFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getAll()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        setState(
            when {
                tracks.isNotEmpty() -> FavoriteTracksState.Content(tracks)
                else -> FavoriteTracksState.Empty
            }
        )
    }

    private fun setState(state: FavoriteTracksState) {
        _stateFlow.value = state
    }
}