package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.resources.TracksSearchState

interface TracksRepository {
    fun searchTracks(term: String): TracksSearchState
}