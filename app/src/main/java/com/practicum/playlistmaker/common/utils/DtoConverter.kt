package com.practicum.playlistmaker.common.utils

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.utils.Extensions.toDate
import com.practicum.playlistmaker.medialibrary.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import java.time.Instant

object DtoConverter {

    fun List<TrackDto>.toTracksList() = map {
        Track(
            it.trackId,
            it.trackName,
            it.artistName,
            it.trackTimeMillis,
            it.artworkUrl100,
            it.collectionName,
            it.releaseDate?.toDate(),
            it.primaryGenreName,
            it.country,
            it.previewUrl!!,
            it.artistViewUrl,
        )
    }

    fun Track.toFavoriteTrackEntity() = FavoriteTrackEntity(
        id,
        trackName,
        artistName,
        duration,
        albumCover,
        albumName,
        releaseDate,
        genre,
        country,
        previewUrl,
        artistViewUrl,
        true,
        Instant.now().toEpochMilli(),
    )

    fun Track.toPlaylistTrackEntity() = PlaylistTrackEntity(
        id,
        trackName,
        artistName,
        duration,
        albumCover,
        albumName,
        releaseDate,
        genre,
        country,
        previewUrl,
        artistViewUrl,
        isFavorite,
        Instant.now().toEpochMilli(),
    )

    fun Playlist.toPlaylistEntity() = PlaylistEntity(
        id,
        name,
        description,
        cover,
        tracks,
        tracksCount,
    )

    fun List<FavoriteTrackEntity>.favoriteTracksToTracks() = map {
        Track(
            it.id,
            it.trackName,
            it.artistName,
            it.duration,
            it.albumCover,
            it.albumName,
            it.releaseDate,
            it.genre,
            it.country,
            it.previewUrl,
            it.artistViewUrl,
            it.isFavorite,
        )
    }

    fun List<PlaylistTrackEntity>.playlistTracksToTracks() = map {
        Track(
            it.id,
            it.trackName,
            it.artistName,
            it.duration,
            it.albumCover,
            it.albumName,
            it.releaseDate,
            it.genre,
            it.country,
            it.previewUrl,
            it.artistViewUrl,
            it.isFavorite,
        )
    }
}