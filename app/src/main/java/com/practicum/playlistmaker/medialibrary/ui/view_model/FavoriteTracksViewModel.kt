package com.practicum.playlistmaker.medialibrary.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.FavoriteTracksState
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
) : ViewModel() {

    private val _liveData = MutableLiveData<FavoriteTracksState>()
    val liveData: LiveData<FavoriteTracksState> get() = _liveData

    fun showFavoriteTracks() {
        setState(FavoriteTracksState.Loading)
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
        _liveData.postValue(state)
    }
}