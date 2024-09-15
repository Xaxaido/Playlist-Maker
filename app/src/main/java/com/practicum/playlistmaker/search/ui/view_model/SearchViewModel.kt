package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.common.resources.SearchState
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.main.domain.api.InternetConnectionCallback
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksConsumer
import com.practicum.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    internetConnectionInteractor: InternetConnectionInteractor,
) : ViewModel(), InternetConnectionCallback {

    private val consumer = object : TracksConsumer {

        override fun consume(tracks: List<Track>?, error: Int?) {
            when {
                !tracks.isNullOrEmpty() -> setState(SearchState.TrackSearchResults(results = tracks))
                else -> {
                    if (error != null) setState(SearchState.ConnectionError(error = error))
                    else setState(SearchState.NothingFound)
                }
            }
        }
    }
    private var searchQuery = ""
    private var hasInternet = false
    private val timer: Debounce by lazy {
        Debounce(delay = Util.USER_INPUT_DELAY) { doSearch(searchQuery) }
    }
    private val _liveData = MutableLiveData<SearchState>()
    val liveData: LiveData<SearchState> = _liveData

    init {
        internetConnectionInteractor.setCallback(this)
    }

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

    fun stopSearch() { tracksInteractor.cancelRequest() }
    fun addToHistory(track: Track) { searchHistoryInteractor.addTrack(track) }
    fun removeFromHistory(pos: Int) { searchHistoryInteractor.removeTrack(pos) }
    private fun setState(state: SearchState) { _liveData.postValue(state) }

    private fun doSearch(term: String) {
        if (!hasInternet) {
            setState(SearchState.ConnectionError(error = Util.NO_CONNECTION))
            return
        }

        setState(SearchState.Loading)
        tracksInteractor.searchTracks(term, consumer)
    }

    override fun onConnected() {
        hasInternet = true
    }

    override fun onDisconnected() {
        hasInternet = false
    }

    override fun onCleared() {
        timer.stop()
    }
}