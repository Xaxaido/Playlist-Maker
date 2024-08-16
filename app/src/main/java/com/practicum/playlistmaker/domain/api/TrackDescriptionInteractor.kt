package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.TrackDescriptionSearchState

interface TrackDescriptionInteractor {
    fun searchTrackDescription(term: String, consumer: TracksDescriptionConsumer)

    interface TracksDescriptionConsumer {
        fun consume(result: TrackDescriptionSearchState)
    }
}