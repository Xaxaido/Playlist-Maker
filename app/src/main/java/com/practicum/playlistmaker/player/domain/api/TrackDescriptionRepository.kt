package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.TrackDescription
import kotlinx.coroutines.flow.Flow

interface TrackDescriptionRepository {
    fun searchTrackDescription(term: String): Flow<TrackDescription>
}