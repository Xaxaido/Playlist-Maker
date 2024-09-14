package com.practicum.playlistmaker.player.domain.api

interface TrackDescriptionInteractor {
    fun searchTrackDescription(term: String, consumer: TracksDescriptionConsumer)
}