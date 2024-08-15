package com.practicum.playlistmaker.extension.util

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.Track.Companion.toDate
import com.practicum.playlistmaker.extension.util.Util.millisToSeconds

object Mapper {

    fun TrackDto.toTrack() =
        Track(
            trackId,
            trackName,
            artistName,
            trackTimeMillis.millisToSeconds(),
            artworkUrl100,
            collectionName,
            releaseDate?.toDate(),
            primaryGenreName,
            country,
            previewUrl,
            artistViewUrl,
        )
}