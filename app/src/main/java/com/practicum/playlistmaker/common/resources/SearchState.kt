package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.search.domain.model.Track

sealed class SearchState(
    val term: String?,
) {

    data object NoData: SearchState(null)
    data object Loading: SearchState(null)
    class NothingFound(
        term: String,
    ): SearchState(term)
    class ConnectionError(
        val error: Int,
        term: String,
    ) : SearchState(term)
    class TrackSearchResults(
        val results: List<Track>,
        term: String,
    ) : SearchState(term)
    class TrackSearchHistory(
        val history: List<Track>,
        val isDataSetChanged: Boolean,
    ) : SearchState(null)
}