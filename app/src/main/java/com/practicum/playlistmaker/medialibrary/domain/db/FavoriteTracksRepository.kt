package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.medialibrary.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun add(track: FavoriteTrackEntity)
    suspend fun remove(track: FavoriteTrackEntity)
    fun getAll(): Flow<List<Track>>
    fun getIds(): Flow<List<Long>>
}