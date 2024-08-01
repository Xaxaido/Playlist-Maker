package com.practicum.playlistmaker.data.model.entity

import java.io.Serializable

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val artistViewUrl: String,
) : Serializable {

    fun getPlayerAlbumCover() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg") ?: ""

}