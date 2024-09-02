package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface SearchState {

    data object Loading: SearchState
    data object NothingFound: SearchState
    data class InternetResults(
        val results: List<Track>
    ) : SearchState
    data class InternetHistory(
        val history: List<Track>
    ) : SearchState
    data class ConnectionError(
        val error: Int
    ) : SearchState
}