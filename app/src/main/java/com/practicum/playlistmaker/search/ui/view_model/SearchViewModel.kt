package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.common.resources.SearchState
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Extensions
import com.practicum.playlistmaker.common.utils.internet.InternetConnectionCallback
import com.practicum.playlistmaker.common.utils.internet.InternetConnectionObserver
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel(), InternetConnectionCallback {

    private val consumer = object : TracksInteractor.TracksConsumer {

        override fun consume(tracks: List<Track>?, error: Int?) {
            when {
                !tracks.isNullOrEmpty() -> setState(SearchState.SearchResults(results = tracks))
                else -> {
                    if (error != null) setState(SearchState.ConnectionError(error = error))
                    else setState(SearchState.NothingFound)
                }
            }
        }
    }
    private var searchQuery = ""
    private var hasInternet = false
    private val _liveData = MutableLiveData<SearchState>()
    val liveData: LiveData<SearchState> = _liveData

    private val timer: Debounce by lazy {
        Debounce(delay = Util.USER_INPUT_DELAY) { doSearch(searchQuery) }
    }

    init {
        InternetConnectionObserver
            .instance(Creator.context)
            .setCallback(this)
            .register()
    }

    fun search(term: String) {
        searchQuery = term
        timer.start()
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        setState(SearchState.SearchResults(emptyList()))
    }

    fun addToHistory(track: Track) { searchHistoryInteractor.addTrack(track) }
    fun getHistory() = searchHistoryInteractor.getHistory()

    private fun setState(state: SearchState) { _liveData.postValue(state) }

    private fun doSearch(term: String) {
        if (searchQuery.isEmpty()) return
        if (!hasInternet) {
            setState(SearchState.ConnectionError(error = Extensions.NO_CONNECTION))
            return
        }

        setState(SearchState.Loading)
        tracksInteractor.searchTracks(term, consumer)
    }

    override fun onConnected() { hasInternet = true }
    override fun onDisconnected() { hasInternet = false }
    override fun onCleared() {
        timer.stop()
        InternetConnectionObserver.unRegister()
    }

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    Creator.getTracksInteractor(),
                    Creator.getSearchHistoryInteractor(),
                )
            }
        }
    }
}