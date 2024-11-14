package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository,
) : FavoriteTracksInteractor {

    override suspend fun add(track: TrackEntity) {
        repository.add(track)
    }

    override suspend fun remove(track: TrackEntity) {
        repository.remove(track)
    }

    override fun getAll(): Flow<List<Track>> {
        return repository.getAll()
    }

    override fun isFavorite(id: Long): Flow<Boolean> {
        return repository.isFavorite(id)
    }
}