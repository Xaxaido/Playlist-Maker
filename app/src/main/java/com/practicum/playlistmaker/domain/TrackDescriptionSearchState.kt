package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.models.TrackDescription

sealed class TrackDescriptionSearchState(
    val description: TrackDescription? = null,
    val error: Int? = null
) {
    class Success(description: TrackDescription?) : TrackDescriptionSearchState(description)
    class Error(error: Int) : TrackDescriptionSearchState(error = error)
}