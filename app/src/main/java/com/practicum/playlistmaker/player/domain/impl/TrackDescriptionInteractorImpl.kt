package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import kotlinx.coroutines.flow.Flow

class TrackDescriptionInteractorImpl(
    private val repository: TrackDescriptionRepository,
) : TrackDescriptionInteractor {

    override fun searchTrackDescription(term: String): Flow<TrackDescription> {
        return repository.searchTrackDescription(term)
    }
}