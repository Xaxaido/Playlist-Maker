package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.resources.SearchState
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.main.domain.api.InternetConnectListener
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
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
    private val _liveData = MutableLiveData<SearchState>(SearchState.NoData)
    val liveData: LiveData<SearchState> = _liveData

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
        markFavorites(searchHistoryInteractor.history,  SearchState.TrackSearchHistory(
            searchHistoryInteractor.history,
            isDatasetChanged,
        ))
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        setState(SearchState.TrackSearchHistory(emptyList(), false))
    }

    fun addToHistory(track: Track) { searchHistoryInteractor.addTrack(track) }
    fun removeFromHistory(pos: Int) { searchHistoryInteractor.removeTrack(pos) }
    private fun setState(state: SearchState) { _liveData.postValue(state) }

    private fun doSearch(term: String) {
        if (term.isEmpty()) return
        if (!hasInternet) {
            setState(SearchState.ConnectionError(Util.NO_CONNECTION, term))
            return
        }

        setState(SearchState.Loading)
        viewModelScope.launch {
            tracksInteractor.searchTracks(term)
                .collect { result ->
                    processResult(result.tracks, result.error, result.term)
                }
        }
    }

    private fun processResult(tracks: List<Track>?, error: Int?, term: String) {
        when {
            !tracks.isNullOrEmpty() -> {
                markFavorites(tracks, SearchState.TrackSearchResults(tracks, term))
            }
            else -> {
                if (error != null) setState(SearchState.ConnectionError(error, term))
                else setState(SearchState.NothingFound(term))
            }
        }
    }

    private fun markFavorites(tracks: List<Track>, state: SearchState) {
        favoriteTracksInteractor.markFavorites(viewModelScope, tracks) { list ->
            when (state) {
                is SearchState.TrackSearchHistory -> state.history = list
                is SearchState.TrackSearchResults -> state.results = list
                else -> {}
            }
            setState(state)
        }
    }

    override fun onConnectionStatusUpdate(hasInternet: Boolean) {
        this.hasInternet = hasInternet
    }

    override fun onCleared() {
        timer.stop()
        internetConnectionInteractor.removeOnInternetConnectListener(this)
    }
}