package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.TrackDescriptionSearchState

interface TrackDescriptionRepository {
    fun searchTrackDescription(term: String): TrackDescriptionSearchState
}