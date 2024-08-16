package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.models.Track

sealed class TracksSearchState(
    val tracks: List<Track> = emptyList(),
    val error: Int? = null
) {
    class Success(tracks: List<Track>) : TracksSearchState(tracks)
    class Error(error: Int) : TracksSearchState(error = error)
}