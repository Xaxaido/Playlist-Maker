package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    suspend fun add(track: TrackEntity)
    suspend fun remove(track: TrackEntity)
    fun getAll(): Flow<List<Track>>
    fun getIds(): Flow<List<Long>>
    fun isFavorite(id: Long): Flow<Boolean>
    fun markFavorites(tracks: List<Track>): Flow<List<Track>>
    fun addToFavorites(
        scope: CoroutineScope,
        track: Track,
        action: (Boolean) -> Unit,
    )
}