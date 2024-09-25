package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun trackToJson(track: Track): String
    fun searchTracks(term: String): TracksSearchState
    fun cancelRequest()
}