package com.practicum.playlistmaker.medialibrary.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.medialibrary.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.medialibrary.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.medialibrary.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.medialibrary.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity

@Database(
    version = 1,
    entities = [
        FavoriteTrackEntity::class,
        PlaylistTrackEntity::class,
        PlaylistEntity::class,
    ]
)

abstract class AppDataBase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
    abstract fun playlistDao(): PlaylistDao
}