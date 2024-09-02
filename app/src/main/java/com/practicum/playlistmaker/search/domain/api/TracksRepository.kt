package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.common.resources.TracksRequestState

interface TracksRepository {
    fun searchTracks(term: String): TracksRequestState
}