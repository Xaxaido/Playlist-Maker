package com.practicum.playlistmaker.data.dto

class TracksSearchResponse(
    val text: String,
    val results: List<TrackDto>
) : Response()