package com.practicum.playlistmaker.medialibrary.data.db.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTracksEntity
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

class PlaylistsRepositoryImpl(
    private val context: Context,
    private val dataBase: AppDataBase,
) : PlaylistsRepository {

    override suspend fun add(playlist: PlaylistEntity) {
        dataBase.playlistDao().add(playlist)
    }

    override suspend fun addToPlaylist(
        playlist: PlaylistEntity,
        playlistTracks: PlaylistTracksEntity,
        playlistTrack: PlaylistTrackEntity,
    ) {
        dataBase.playlistDao()
            .addTrack(playlist, playlistTracks, playlistTrack)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return dataBase.playlistDao().getAllPlaylists()
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Long): Boolean {
        return dataBase.playlistDao().isTrackInPlaylist(playlistId, trackId)
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