package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.TrackDescription
import kotlinx.coroutines.flow.Flow

interface TrackDescriptionInteractor {
    fun searchTrackDescription(term: String): Flow<TrackDescription>
}