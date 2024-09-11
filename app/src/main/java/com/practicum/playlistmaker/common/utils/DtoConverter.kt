package com.practicum.playlistmaker.common.utils

import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.settings.data.dto.ThemeSettingsDto
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.model.TrackParcelable
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.utils.Extensions.toDate

object DtoConverter {

    fun TrackDto.toTrack() = Track(
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

    fun TrackParcelable.toTrack() = Track(
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
    )

    fun Track.toTrackParcelable() = TrackParcelable(
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
    )

    fun ThemeSettingsDto.toThemeSettings() = ThemeSettings(appTheme)
}