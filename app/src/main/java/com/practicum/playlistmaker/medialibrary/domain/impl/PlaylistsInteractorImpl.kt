package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTracksEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository,
) : PlaylistsInteractor {

    override fun saveImage(uri: String): String {
        return repository.saveImage(uri)
    }

    override suspend fun add(playlist: PlaylistEntity) {
        repository.add(playlist)
    }

    override suspend fun addToPlaylist(
        playlist: PlaylistEntity,
        playlistTracks: PlaylistTracksEntity,
        playlistTrack: PlaylistTrackEntity
    ) {
        repository.addToPlaylist(playlist, playlistTracks, playlistTrack)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Long): Boolean {
        return repository.isTrackInPlaylist(playlistId, trackId)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return repository.getAll()
    }
}