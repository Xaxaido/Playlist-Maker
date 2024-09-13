package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface TracksConsumer {
    fun consume(tracks: List<Track>?, error: Int?)
}