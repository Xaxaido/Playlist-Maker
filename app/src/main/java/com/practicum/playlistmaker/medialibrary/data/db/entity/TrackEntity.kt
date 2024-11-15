package com.practicum.playlistmaker.medialibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class TrackEntity(
    @PrimaryKey
    val id: Long,
    val trackName: String,
    val artistName: String,
    val duration: String,
    val albumCover: String?,
    val albumName: String?,
    val releaseDate: String?,
    val genre: String?,
    val country: String?,
    val previewUrl: String,
    val artistViewUrl: String?,
    val dateAdded: Long,
)