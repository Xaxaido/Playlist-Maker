package com.practicum.playlistmaker.search.domain.api

interface TracksInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)
    fun cancelRequest()
}