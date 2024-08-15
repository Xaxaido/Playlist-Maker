package com.practicum.playlistmaker.domain.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val artistViewUrl: String,
) : Serializable {

    fun getPlayerAlbumCover() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg") ?: ""

    companion object {

        fun String.toDate() = SimpleDateFormat("yyyy", Locale.getDefault())
            .parse(this)
            ?.toInstant()
            ?.atZone(ZoneId.systemDefault())
            ?.year
            ?.toString()
            ?: ""
    }
}