package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.domain.api.TrackDescriptionRepository
import java.util.concurrent.Executors

class TrackDescriptionInteractorImpl(private val repository: TrackDescriptionRepository) : TrackDescriptionInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrackDescription(term: String, consumer: TrackDescriptionInteractor.TracksDescriptionConsumer) {
        executor.execute {
            consumer.consume(repository.searchTrackDescription(term))
        }
    }
}