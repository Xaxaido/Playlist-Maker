package com.practicum.playlistmaker.medialibrary.data.db.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.common.utils.DtoConverter.toPlaylistTrackEntity
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

class PlaylistsRepositoryImpl(
    private val context: Context,
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
        return dataBase.playlistDao().observe()
    }

    override fun getIds(): Flow<List<Long>> {
        return dataBase.playlistTrackDao().getIds()
    }

    override fun getTracks(json: String?): MutableList<Long> {
        return if (!json.isNullOrBlank()) {
            gson.fromJson(json, object : TypeToken<List<Long>>() {}.type) ?: mutableListOf()
        } else mutableListOf()
    }

    override fun saveImage(uri: String): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val fileName = "cover_${Instant.now().toEpochMilli()}.jpg"
        val file = File(filePath, fileName)
        val inputStream = context.contentResolver.openInputStream(Uri.parse(uri))
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        return file.path
    }
}