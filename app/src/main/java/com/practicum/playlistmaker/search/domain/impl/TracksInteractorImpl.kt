package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.search.domain.api.TracksConsumer
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TracksInteractorImpl @Inject constructor(
    private val repository: TracksRepository,
) : TracksInteractor {

    override fun trackToJson(track: Track) = repository.trackToJson(track)

    override fun searchTracks(term: String, page: Int, consumer: TracksConsumer) {
        CoroutineScope(Dispatchers.IO).launch {
            when(val result = repository.searchTracks(term, page)) {
                is TracksSearchState.Success -> { consumer.consume(result.tracks, null) }
                is TracksSearchState.Error -> { consumer.consume(null, result.error) }
            }
        }
    }

    override fun cancelRequest() { repository.cancelRequest() }
}