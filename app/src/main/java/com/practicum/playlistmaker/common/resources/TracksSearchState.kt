package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.search.domain.model.Track

sealed class TracksSearchState(
    var tracks: List<Track> = emptyList(),
    val error: Int? = null,
    val term: String = "",
) {
    class Success(tracks: List<Track>, term: String) : TracksSearchState(tracks, term = term)
    class Error(error: Int, term: String) : TracksSearchState(error = error, term = term)
}