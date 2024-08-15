package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.resources.TracksSearchState

interface TracksMediator {
    fun searchTracks(term: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: TracksSearchState)
    }
}