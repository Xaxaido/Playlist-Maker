package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.medialibrary.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    suspend fun add(track: FavoriteTrackEntity)
    suspend fun remove(track: FavoriteTrackEntity)
    fun getAll(): Flow<List<Track>>
    fun getIds(): Flow<List<Long>>
    fun markFavorites(tracks: List<Track>): Flow<List<Track>>
    fun addToFavorites(scope: CoroutineScope, track: Track)
}