package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {

    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE id = :trackId")
    suspend fun remove(trackId: Long)

    @Query("SELECT * FROM playlist_tracks")
    suspend fun getAll(): List<PlaylistTrackEntity>

    @Query("SELECT id FROM playlist_tracks")
    fun getIds(): Flow<List<Long>>
}