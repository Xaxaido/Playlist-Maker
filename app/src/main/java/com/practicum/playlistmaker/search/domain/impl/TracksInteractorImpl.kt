package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.common.resources.TracksRequestState
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when(val result = repository.searchTracks(term)) {
                is TracksRequestState.Success -> { consumer.consume(result.tracks, null) }
                is TracksRequestState.Error -> { consumer.consume(null, result.error) }
            }
        }
    }

    override fun cancelRequest() { repository.cancelRequest() }
}