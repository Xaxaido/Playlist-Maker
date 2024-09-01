package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.player.domain.model.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}