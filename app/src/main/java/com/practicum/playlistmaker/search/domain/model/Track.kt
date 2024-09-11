package com.practicum.playlistmaker.search.domain.model

data class Track(
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
    val artistViewUrl: String,
) {

    fun getPlayerAlbumCover() = albumCover?.replaceAfterLast('/', "512x512bb.jpg") ?: ""
}