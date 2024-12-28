package com.practicum.playlistmaker.medialibrary.data.db.impl

import com.practicum.playlistmaker.common.utils.DtoConverter.playlistTracksToTracks
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val dataBase: AppDataBase,
) : PlaylistRepository {

    override suspend fun removePlaylist(playlistId: Int) {
        dataBase.playlistDao().remove(playlistId)
    }

    override suspend fun removeTrack(playlist: PlaylistEntity, trackId: Long) {
        dataBase.playlistDao()
            .removeTrack(playlist, trackId)
    }

    override fun getPlaylist(id: Int): Flow<Playlist> {
        return dataBase.playlistDao().getPlaylist(id)
    }

    override fun getTracks(playlistId: Int): Flow<List<Track>> = flow {
        val tracks = dataBase.playlistDao()
                        .getAllPlaylistTracks(playlistId)
        emit(tracks.playlistTracksToTracks())
    }
}