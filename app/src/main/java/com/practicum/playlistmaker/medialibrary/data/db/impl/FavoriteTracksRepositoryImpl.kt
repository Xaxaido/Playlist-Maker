package com.practicum.playlistmaker.medialibrary.data.db.impl

import com.practicum.playlistmaker.common.utils.DtoConverter.toTrack
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val dataBase: AppDataBase,
) : FavoriteTracksRepository {

    override suspend fun add(track: TrackEntity) {
        dataBase.trackDao().add(track)
    }

    override suspend fun remove(track: TrackEntity) {
        dataBase.trackDao().remove(track)
    }

    override fun getAll(): Flow<List<Track>> {
        return dataBase.trackDao().getAll().map { tracks ->
            tracks.toTrack()
        }
    }

    override fun getIds(): Flow<List<Long>> {
        return dataBase.trackDao().getIds()
    }
}