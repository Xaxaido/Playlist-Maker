package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.search.domain.api.TracksConsumer
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(term: String, consumer: TracksConsumer) {
        CoroutineScope(Dispatchers.IO).launch {
            when(val result = repository.searchTracks(term)) {
                is TracksSearchState.Success -> { consumer.consume(result.tracks, null) }
                is TracksSearchState.Error -> { consumer.consume(null, result.error) }
            }
        }
    }

    override fun cancelRequest() { repository.cancelRequest() }
}