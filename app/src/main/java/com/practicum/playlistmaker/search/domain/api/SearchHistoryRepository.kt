package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    val history: List<Track>
    fun addTrack(track: Track)
    fun removeTrack(pos: Int)
    fun clearHistory()
}