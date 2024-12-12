package com.practicum.playlistmaker.medialibrary.data.db.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.common.utils.DtoConverter.playlistTracksToTracks
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val dataBase: AppDataBase,
    private val gson: Gson,
) : PlaylistRepository {

    private fun getTracksIds(json: String): MutableList<Long> {
        return gson.fromJson(json, object : TypeToken<List<Long>>() {}.type) ?: mutableListOf()
    }

    private suspend fun removeTrackIfNecessary(trackId: Long) {
        val playlists = dataBase.playlistDao().getAll()

        if (!playlists.any {
            it.tracks?.let { json ->
                val tracks = getTracksIds(json)
                tracks.contains(trackId)
            } == true
        }) {
            dataBase.playlistTrackDao().remove(trackId)
        }
    }

    override suspend fun removeTrack(playlist: Playlist, trackId: Long) {
        val tracks = getTracksIds(playlist.tracks!!)

        tracks.remove(trackId)
        dataBase.playlistDao().addToPlaylist(playlist.id, gson.toJson(tracks), tracks.size)
        removeTrackIfNecessary(trackId)
    }

    override fun getPlaylist(id: Int): Flow<Playlist> {
        return dataBase.playlistDao().getPlaylist(id)
    }

    override fun getTracks(json: String?): Flow<List<Track>> = flow {
        if (json.isNullOrBlank()) {
            emit(emptyList())
        } else {
            val ids = getTracksIds(json)
            val tracks = dataBase.playlistTrackDao().getAll()

            emit(tracks.filter { ids.contains(it.id) }.playlistTracksToTracks())
        }
    }
}