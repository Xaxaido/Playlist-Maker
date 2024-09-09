package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.search.domain.model.Track

sealed class TracksSearchState(
    val tracks: List<Track> = emptyList(),
    val error: Int? = null
) {
    class Success(tracks: List<Track>) : TracksSearchState(tracks)
    class Error(error: Int) : TracksSearchState(error = error)
}