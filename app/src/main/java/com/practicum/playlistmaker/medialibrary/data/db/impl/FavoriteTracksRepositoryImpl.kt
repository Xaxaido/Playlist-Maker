package com.practicum.playlistmaker.medialibrary.data.db.impl

import com.practicum.playlistmaker.common.utils.DtoConverter.toTrack
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val dataBase: AppDataBase,
) : FavoriteTracksRepository {

    override suspend fun add(track: TrackEntity) {
        dataBase.trackDao().add(track)
    }

    override suspend fun remove(track: TrackEntity) {
        dataBase.trackDao().remove(track)
    }

    override fun getAll(): Flow<List<Track>> = flow {
        val tracks = dataBase.trackDao().getAll()
        emit(tracks.toTrack())
    }

    override fun getIds(): Flow<List<Long>> = flow {
        val ids = dataBase.trackDao().getIds()
        emit(ids)
    }

    override fun isFavorite(id: Long): Flow<Boolean> = flow {
        val isInFavorite = dataBase.trackDao().isFavorite(id)
        emit(isInFavorite)
    }
}