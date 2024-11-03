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
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import kotlinx.coroutines.launch

class SearchViewModel(
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

    fun trackToJson(track: Track) = tracksInteractor.trackToJson(track)

    fun search(term: String) {
        searchQuery = term
        timer.start()
    }

    fun getHistory(isDatasetChanged: Boolean) {
        setState(
            SearchState.TrackSearchHistory(
                searchHistoryInteractor.history,
                isDatasetChanged,
            )
        )
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        setState(SearchState.TrackSearchResults(emptyList()))
    }

    fun addToHistory(track: Track) { searchHistoryInteractor.addTrack(track) }
    fun removeFromHistory(pos: Int) { searchHistoryInteractor.removeTrack(pos) }
    private fun setState(state: SearchState) { _liveData.postValue(state) }

    private fun doSearch(term: String) {
        if (term.isEmpty()) return
        if (!hasInternet) {
            setState(SearchState.ConnectionError(error = Util.NO_CONNECTION))
            return
        }

        setState(SearchState.Loading)
        viewModelScope.launch {
            tracksInteractor.searchTracks(term)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }

    private fun processResult(tracks: List<Track>?, error: Int?) {
        when {
            !tracks.isNullOrEmpty() -> {
                setState(SearchState.TrackSearchResults(results = tracks))
            }
            else -> {
                if (error != null) setState(SearchState.ConnectionError(error = error))
                else setState(SearchState.NothingFound)
            }
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