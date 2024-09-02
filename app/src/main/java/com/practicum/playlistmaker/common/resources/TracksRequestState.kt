package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.search.domain.model.Track

sealed class TracksRequestState(
    val tracks: List<Track> = emptyList(),
    val error: Int? = null
) {
    class Success(tracks: List<Track>) : TracksRequestState(tracks)
    class Error(error: Int) : TracksRequestState(error = error)
}