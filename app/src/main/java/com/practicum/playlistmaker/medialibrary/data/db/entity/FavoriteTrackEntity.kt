package com.practicum.playlistmaker.medialibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
    @PrimaryKey
    val id: Long,
    val trackName: String,
    val artistName: String,
    val duration: Long,
    val albumCover: String?,
    val albumName: String?,
    val releaseDate: String?,
    val genre: String?,
    val country: String?,
    val previewUrl: String,
    val artistViewUrl: String?,
    val isFavorite: Boolean,
    val dateAdded: Long,
)