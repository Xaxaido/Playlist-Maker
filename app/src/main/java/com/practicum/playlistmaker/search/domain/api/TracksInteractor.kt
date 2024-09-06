package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface TracksInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)
    fun cancelRequest()

    interface TracksConsumer {
        fun consume(tracks: List<Track>?, error: Int?)
    }
}