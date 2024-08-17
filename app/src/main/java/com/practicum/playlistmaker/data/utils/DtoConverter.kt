package com.practicum.playlistmaker.data.utils

import com.practicum.playlistmaker.common.Util.millisToSeconds
import com.practicum.playlistmaker.data.dto.ThemeSettingsDto
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.models.ThemeSettings
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.utils.Extensions.toDate

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

    fun ThemeSettingsDto.toThemeSettings() = ThemeSettings(appTheme)
}