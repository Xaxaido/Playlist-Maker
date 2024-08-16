package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.TracksSearchState

interface TracksInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: TracksSearchState)
    }
}