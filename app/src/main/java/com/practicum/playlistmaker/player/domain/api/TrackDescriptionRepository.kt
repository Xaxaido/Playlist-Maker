package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.TrackDescription

interface TrackDescriptionRepository {
    fun searchTrackDescription(term: String): TrackDescription
}