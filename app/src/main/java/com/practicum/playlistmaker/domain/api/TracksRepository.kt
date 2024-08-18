package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.TracksSearchState

interface TracksRepository {
    fun searchTracks(term: String): TracksSearchState
}