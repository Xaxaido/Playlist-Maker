package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun remove(track: TrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<TrackEntity>>

    @Query("SELECT id FROM favorite_tracks")
    fun getIds(): Flow<List<Long>>
}