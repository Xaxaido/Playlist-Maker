package com.practicum.playlistmaker.medialibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_tracks")
data class PlaylistTracksEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val playlistId: Int,
    val trackId: Long,
)