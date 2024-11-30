package com.practicum.playlistmaker.medialibrary.data.db.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.common.utils.DtoConverter.toPlaylistTrackEntity
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsRepositoryImpl(
    private val dataBase: AppDataBase,
    private val gson: Gson,
) : PlaylistsRepository {

    override suspend fun add(playlist: PlaylistEntity) {
        dataBase.playlistDao().add(playlist)
    }

    override suspend fun remove(playlist: PlaylistEntity) {
        dataBase.playlistDao().remove(playlist)
    }

    override suspend fun addToPlaylist(playlist: Playlist, track: Track) {
        val tracks = getTracks(playlist.tracks)

        tracks.add(track.id)
        dataBase.playlistDao().addToPlaylist(playlist.id, gson.toJson(tracks), tracks.count())
        dataBase.playlistTrackDao().add(track.toPlaylistTrackEntity())
    }

    override suspend fun updatePlaylistCover(path: String) {
        dataBase.playlistDao().updatePlaylistCover(path)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return dataBase.playlistDao().getAll()
    }

    override fun getIds(): Flow<List<Long>> {
        return dataBase.playlistTrackDao().getIds()
    }

    override fun getTracks(json: String?): MutableList<Long> {
        return if (!json.isNullOrBlank()) {
            gson.fromJson(json, object : TypeToken<List<Long>>() {}.type) ?: mutableListOf()
        } else mutableListOf()
    }
}