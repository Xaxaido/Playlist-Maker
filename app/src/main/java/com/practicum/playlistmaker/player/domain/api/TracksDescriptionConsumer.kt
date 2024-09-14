package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.TrackDescription

interface TracksDescriptionConsumer {
    fun consume(result: TrackDescription)
}