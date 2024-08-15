package com.practicum.playlistmaker.data.resources

import com.practicum.playlistmaker.domain.models.Track

sealed class TracksSearchState(val tracks: List<Track>, val error: Int? = null) {
    class Success(tracks: List<Track>) : TracksSearchState(tracks)
    class Error(tracks: List<Track>, error: Int) : TracksSearchState(tracks, error)
}