package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun add(playlist: PlaylistEntity)
    suspend fun remove(playlist: PlaylistEntity)
    suspend fun addToPlaylist(playlist: Playlist, track: Track)
    suspend fun updatePlaylistCover(path: String)
    fun getAll(): Flow<List<Playlist>>
    fun getIds(): Flow<List<Long>>
    fun getTracks(json: String?): MutableList<Long>
}