package com.practicum.playlistmaker.common.utils

import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.utils.Extensions.toDate

object DtoConverter {

    fun List<TrackDto>.toTracksList() = map {
        Track(
            it.trackId,
            it.trackName,
            it.artistName,
            it.trackTimeMillis.millisToSeconds(),
            it.artworkUrl100,
            it.collectionName,
            it.releaseDate?.toDate(),
            it.primaryGenreName,
            it.country,
            it.previewUrl!!,
            it.artistViewUrl,
        )
    }
}