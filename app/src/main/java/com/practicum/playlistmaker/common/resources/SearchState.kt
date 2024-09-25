package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface SearchState {

    data object Loading: SearchState
    data object NothingFound: SearchState
    data class ConnectionError(
        val error: Int
    ) : SearchState
    data class TrackSearchResults(
        val results: List<Track>
    ) : SearchState
    data class TrackSearchHistory(
        val history: List<Track>,
        val isDataSetChanged: Boolean,
    ) : SearchState
    data class SendTrackToPlayer(
        val json: String
    ) : SearchState
}