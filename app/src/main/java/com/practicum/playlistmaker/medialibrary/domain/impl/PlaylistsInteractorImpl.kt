package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository,
) : PlaylistsInteractor {

    override suspend fun add(playlist: PlaylistEntity) {
        repository.add(playlist)
    }

    override suspend fun remove(playlist: PlaylistEntity) {
        repository.remove(playlist)
    }

    override suspend fun addToPlaylist(playlist: Playlist, track: Track) {
        repository.addToPlaylist(playlist, track)
    }

    override suspend fun updatePlaylistCover(path: String) {
        repository.updatePlaylistCover(path)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return repository.getAll()
    }

    override fun getIds(): Flow<List<Long>> {
        return repository.getIds()
    }

    override fun getTracks(json: String?): MutableList<Long> {
        return repository.getTracks(json)
    }
}