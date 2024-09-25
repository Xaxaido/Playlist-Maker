package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.player.domain.api.TracksDescriptionConsumer
import java.util.concurrent.Executors
import javax.inject.Inject

class TrackDescriptionInteractorImpl @Inject constructor(
    private val repository: TrackDescriptionRepository,
) : TrackDescriptionInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrackDescription(term: String, consumer: TracksDescriptionConsumer) {
        executor.execute {
            consumer.consume(repository.searchTrackDescription(term))
        }
    }
}