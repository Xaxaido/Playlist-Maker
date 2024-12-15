package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository,
) : PlaylistInteractor {

    override suspend fun removePlaylist(playlist: PlaylistEntity) {
        repository.removePlaylist(playlist)
    }

    override suspend fun removeTrack(playlist: Playlist, trackId: Long) {
        repository.removeTrack(playlist, trackId)
    }

    override fun getPlaylist(id: Int): Flow<Playlist> {
        return repository.getPlaylist(id)
    }

    override fun getTracks(json: String?): Flow<List<Track>> {
        return repository.getTracks(json)
    }
}