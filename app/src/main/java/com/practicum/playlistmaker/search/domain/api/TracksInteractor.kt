package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface TracksInteractor {
    fun trackToJson(track: Track): String
    fun searchTracks(term: String, consumer: TracksConsumer)
    fun cancelRequest()
}