package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksMediator
import com.practicum.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksMediatorImpl(private val repository: TracksRepository) : TracksMediator {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: TracksMediator.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(term))
        }
    }
}