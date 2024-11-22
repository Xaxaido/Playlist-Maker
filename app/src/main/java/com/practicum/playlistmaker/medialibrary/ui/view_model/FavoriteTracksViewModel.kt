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

    private val _stateFlow = MutableStateFlow<FavoriteTracksState>(FavoriteTracksState.Default)
    val stateFlow: StateFlow<FavoriteTracksState> = _stateFlow.asStateFlow()

    fun addToFavorites(track: Track) {
        favoriteTracksInteractor.addToFavorites(viewModelScope, track) {
            showFavoriteTracks()
        }
    }

    fun showFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getAll()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        when {
            tracks.isNotEmpty() -> {
                setState(FavoriteTracksState.Content(tracks))
            }
            else -> {
                setState(FavoriteTracksState.Empty)
            }
        }
    }

    private fun setState(state: FavoriteTracksState) {
        _stateFlow.value = state
    }
}