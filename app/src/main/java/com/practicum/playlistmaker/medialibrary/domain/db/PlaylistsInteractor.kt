package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTracksEntity
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun add(playlist: PlaylistEntity)
    suspend fun addToPlaylist(
        playlist: PlaylistEntity,
        playlistTracks: PlaylistTracksEntity,
        playlistTrack: PlaylistTrackEntity,
    )
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Long): Boolean
    fun getAll(): Flow<List<Playlist>>
    fun saveImage(uri: String): String
}