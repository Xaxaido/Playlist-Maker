package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun trackToJson(track: Track): String
    fun searchTracks(term: String, page: Int = 0): Flow<Pair<List<Track>?, Int?>>
}