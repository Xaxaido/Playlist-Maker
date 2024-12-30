package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface SearchHistoryInteractor {
    val history: List<Track>
    fun updateTracks(tracks: List<Track>)
    fun addTrack(track: Track)
    fun removeTrack(pos: Int)
    fun clearHistory()
}