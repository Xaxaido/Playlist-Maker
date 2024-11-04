package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(
    private val repository: TracksRepository,
) : TracksInteractor {

    override fun trackToJson(track: Track) = repository.trackToJson(track)

    override fun searchTracks(term: String, page: Int): Flow<Pair<List<Track>?, Int?>> {
        return repository.searchTracks(term, page).map { result ->
            when(result) {
                is TracksSearchState.Success -> { Pair(result.tracks, null) }
                is TracksSearchState.Error -> { Pair(null, result.error) }
            }
        }
    }
}