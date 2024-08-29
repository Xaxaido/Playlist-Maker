package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.common.resources.TracksSearchState

interface TracksRepository {
    fun searchTracks(term: String): TracksSearchState
}