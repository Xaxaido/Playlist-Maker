package com.practicum.playlistmaker.medialibrary.domain.model

data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    var cover: String?,
    val tracks: String?,
    val tracksCount: Int,
)