package com.practicum.playlistmaker.medialibrary.data.db.impl

import com.practicum.playlistmaker.common.utils.DtoConverter.toTracks
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.medialibrary.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val dataBase: AppDataBase,
) : FavoriteTracksRepository {

    override suspend fun add(track: FavoriteTrackEntity) {
        dataBase.favoriteTrackDao().add(track)
    }

    override suspend fun remove(track: FavoriteTrackEntity) {
        dataBase.favoriteTrackDao().remove(track)
    }

    override fun getAll(): Flow<List<Track>> {
        return dataBase.favoriteTrackDao().getAll().map { tracks ->
            tracks.toTracks()
        }
    }

    override fun getIds(): Flow<List<Long>> {
        return dataBase.favoriteTrackDao().getIds()
    }
}