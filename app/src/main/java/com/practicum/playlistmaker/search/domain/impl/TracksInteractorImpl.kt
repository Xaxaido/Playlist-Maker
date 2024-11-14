package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val repository: TracksRepository,
) : TracksInteractor {

    override fun trackToJson(track: Track) = repository.trackToJson(track)

    override fun searchTracks(term: String, page: Int): Flow<TracksSearchState> {
        return repository.searchTracks(term, page)
    }
}