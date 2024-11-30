package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(playlist: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun remove(playlist: PlaylistEntity)

    @Query("UPDATE playlists SET tracks = :updatedTracks, tracksCount = :updatedCount WHERE id = :playlistId")
    suspend fun addToPlaylist(playlistId: Int, updatedTracks: String, updatedCount: Int)

    @Query("UPDATE playlists SET cover = :path")
    suspend fun updatePlaylistCover(path: String)

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylist(playlistId: Int): PlaylistEntity

    @Query("SELECT * FROM playlists ORDER BY tracksCount DESC")
    fun getAll(): Flow<List<Playlist>>
}