package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.SearchState
import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.main.domain.api.InternetConnectListener
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val internetConnectionInteractor: InternetConnectionInteractor,
) : ViewModel(), InternetConnectListener {

    private var searchQuery = ""
    private var hasInternet = false
    private val timer: Debounce by lazy {
        Debounce(Util.USER_INPUT_DELAY, viewModelScope) { doSearch(searchQuery) }
    }

    private val _stateFlow = MutableStateFlow<SearchState>(SearchState.NoData)
    val stateFlow: StateFlow<SearchState> = _stateFlow.asStateFlow()

    init {
        internetConnectionInteractor.addOnInternetConnectListener(this)
    }

    fun addToFavorites(track: Track) {
        favoriteTracksInteractor.addToFavorites(viewModelScope, track) {
            setState(SearchState.IsFavorite)
        }
    }

    fun getFavorites(action: (List<Long>) -> Unit) {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getIds()
                .collect {
                    action(it)
                }
        }
    }

    fun search(term: String) {
        searchQuery = term
        timer.start()
    }

    fun getHistory(isDatasetChanged: Boolean) {
        viewModelScope.launch {
            favoriteTracksInteractor
                .markFavorites(searchHistoryInteractor.history)
                .collect { updatedTracks ->
                    setState(SearchState.TrackSearchHistory(updatedTracks, isDatasetChanged))
            }
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        setState(SearchState.TrackSearchHistory(emptyList(), false))
    }

    fun addToHistory(track: Track) { searchHistoryInteractor.addTrack(track) }
    fun removeFromHistory(pos: Int) { searchHistoryInteractor.removeTrack(pos) }
    private fun setState(state: SearchState) { _stateFlow.value = state }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun doSearch(term: String) {
        if (term.isEmpty()) return
        if (!hasInternet) {
            setState(SearchState.ConnectionError(Util.NO_CONNECTION, term))
            return
        }

        setState(SearchState.Loading)
        viewModelScope.launch {
            tracksInteractor.searchTracks(term)
                .flatMapLatest { result ->
                    favoriteTracksInteractor.markFavorites(result.tracks).map { updatedTracks ->
                        when(result) {
                            is TracksSearchState.Success -> TracksSearchState.Success(updatedTracks, result.term)
                            else -> result
                        }
                    }
                }
                .collect { updatedResult ->
                    processResult(updatedResult.tracks, updatedResult.error, updatedResult.term)
                }
        }
    }

    private fun processResult(tracks: List<Track>, error: Int?, term: String) {
        setState(
            when {
                tracks.isNotEmpty() -> SearchState.TrackSearchResults(tracks, term)
                error != null -> SearchState.ConnectionError(error, term)
                else -> SearchState.NothingFound(term)
            }
        )
    }

    override fun onConnectionStatusUpdate(hasInternet: Boolean) {
        this.hasInternet = hasInternet
    }

    override fun onCleared() {
        timer.stop()
        internetConnectionInteractor.removeOnInternetConnectListener(this)
    }
}