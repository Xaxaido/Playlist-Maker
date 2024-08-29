package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.player.domain.model.TrackDescription

interface TrackDescriptionInteractor {
    fun searchTrackDescription(term: String, consumer: TracksDescriptionConsumer)

    interface TracksDescriptionConsumer {
        fun consume(result: TrackDescription)
    }
}