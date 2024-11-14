package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun add(track: TrackEntity)
    suspend fun remove(track: TrackEntity)
    fun getAll(): Flow<List<Track>>
    fun isFavorite(id: Long): Flow<Boolean>
}