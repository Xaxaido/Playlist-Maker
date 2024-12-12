package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.medialibrary.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun removeTrack(playlist: Playlist, trackId: Long)
    fun getPlaylist(id: Int): Flow<Playlist>
    fun getTracks(json: String?): Flow<List<Track>>

}