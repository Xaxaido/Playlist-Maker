package com.practicum.playlistmaker.extension.network

import com.practicum.playlistmaker.data.model.entity.Track

data class TrackResponse(
    val text: String,
    val results: List<Track>
)