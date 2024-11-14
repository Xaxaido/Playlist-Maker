package com.practicum.playlistmaker.search.data.dto

class TracksSearchResponse(
    var text: String,
    val results: List<TrackDto>,
) : Response()