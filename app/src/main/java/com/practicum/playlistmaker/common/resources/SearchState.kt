package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.player.domain.model.Track

sealed interface SearchState {

    data object Loading: SearchState
    data object NothingFound: SearchState
    data class SearchResults(
        val results: List<Track>
    ) : SearchState
    data class SearchHistory(
        val history: List<Track>
    ) : SearchState
    data class ConnectionError(
        val error: Int
    ) : SearchState
}