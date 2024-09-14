package com.practicum.playlistmaker.search.data.dto

class TracksSearchResponse(
    val text: String,
    val results: List<TrackDto>,
) : Response()