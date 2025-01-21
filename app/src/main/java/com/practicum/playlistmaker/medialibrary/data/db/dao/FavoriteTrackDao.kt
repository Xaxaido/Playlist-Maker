package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialibrary.data.db.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(entity = FavoriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(track: FavoriteTrackEntity)

    @Delete(entity = FavoriteTrackEntity::class)
    suspend fun remove(track: FavoriteTrackEntity)

    @Query("SELECT EXISTS (SELECT 1 FROM favorite_tracks WHERE id = :trackId)")
    suspend fun isTrackFavorite(trackId: Long): Boolean

    @Query("SELECT * FROM favorite_tracks ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT id FROM favorite_tracks")
    fun getIds(): Flow<List<Long>>
}