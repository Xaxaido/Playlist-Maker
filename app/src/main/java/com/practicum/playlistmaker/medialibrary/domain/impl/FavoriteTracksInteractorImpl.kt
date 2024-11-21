package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.common.utils.DtoConverter.toTrackEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

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

    override fun getIds(): Flow<List<Long>> {
        return repository.getIds()
    }

    override fun isFavorite(id: Long): Flow<Boolean> {
        return repository.isFavorite(id)
    }

    override fun markFavorites(tracks: List<Track>): Flow<List<Track>> = flow {
        getIds()
            .collect { favorites ->
                val updatedTracks = tracks.map { track ->
                    track.apply { isFavorite = favorites.contains(id) }
                }
                emit(updatedTracks)
            }
    }

    override fun addToFavorites(
        scope: CoroutineScope,
        track: Track,
        action: (Boolean) -> Unit,
    ) {
        scope.launch {
            track.isFavorite = !track.isFavorite
            val entity = track.toTrackEntity()
            if (track.isFavorite) {
                add(entity)
            } else {
                remove(entity)
            }
            action(track.isFavorite)
        }
    }
}