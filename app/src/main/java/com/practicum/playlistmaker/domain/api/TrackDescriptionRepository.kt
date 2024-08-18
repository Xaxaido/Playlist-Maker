package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.TrackDescription

interface TrackDescriptionRepository {
    fun searchTrackDescription(term: String): TrackDescription
}