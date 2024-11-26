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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val internetConnectionInteractor: InternetConnectionInteractor,
) : ViewModel(), InternetConnectListener {

    var isHistoryVisible: Boolean? = null
    private var favoritesList: List<Long> = emptyList()
    private var currentList: List<Track> = emptyList()
    private var hasInternet = false
    private val timer: Debounce<String> by lazy {
        Debounce(Util.USER_INPUT_DELAY, viewModelScope) { term ->
            doSearch(term)
        }
    }

    private val _stateFlow = MutableStateFlow<SearchState>(SearchState.NoData)
    val stateFlow: StateFlow<SearchState> = _stateFlow.asStateFlow()

    init {
        internetConnectionInteractor.addOnInternetConnectListener(this)
        observeFavoriteTracks()
    }

    fun addToFavorites(track: Track) {
        favoriteTracksInteractor.addToFavorites(viewModelScope, track)
    }

    fun search(term: String) {
        timer.start(parameter = term)
    }

    fun getHistory(isDatasetChanged: Boolean) {
        currentList = searchHistoryInteractor.history
        setState(SearchState.TrackSearchHistory(currentList, isDatasetChanged))
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        setState(SearchState.TrackSearchHistory(emptyList(), false))
    }

    fun addToHistory(track: Track) { searchHistoryInteractor.addTrack(track) }
    fun removeFromHistory(pos: Int) { searchHistoryInteractor.removeTrack(pos) }
    private fun setState(state: SearchState) { _stateFlow.value = state }

    private fun observeFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getIds()
                .collect { favorites ->
                    favoritesList = favorites
                    processFavoriteTracks()
            }
        }
    }

    private fun processFavoriteTracks() {
        val updatedTracks = currentList.map { track ->
            track.apply { isFavorite = favoritesList.contains(id) }
        }

        isHistoryVisible?.let {
            if (it) {
                setState(SearchState.TrackSearchHistory(updatedTracks, false))
            } else {
                setState(SearchState.TrackSearchResults(updatedTracks, null, false))
            }
        }
    }

    private fun doSearch(term: String?) {
        if (term.isNullOrEmpty()) return
        if (!hasInternet) {
            setState(SearchState.ConnectionError(Util.NO_CONNECTION, term))
            return
        }

        setState(SearchState.Loading)
        viewModelScope.launch {
            tracksInteractor.searchTracks(term)
                .map { result ->
                    result.tracks.map {track ->
                    track.apply { isFavorite = favoritesList.contains(id) }
                }

                when(result) {
                    is TracksSearchState.Success -> TracksSearchState.Success(result.tracks, result.term)
                    else -> result
                }
            }.collect { result ->
                processResult(result.tracks, result.error, result.term)
            }
        }
    }

    private fun processResult(tracks: List<Track>, error: Int?, term: String) {
        setState(
            when {
                tracks.isNotEmpty() -> {
                    currentList = tracks
                    SearchState.TrackSearchResults(tracks, term)
                }
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