package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun removePlaylist(playlistId: Int)
    suspend fun removeTrack(playlist: PlaylistEntity, trackId: Long)
    fun getPlaylist(id: Int): Flow<Playlist>
    fun getTracks(playlistId: Int): Flow<List<Track>>
}