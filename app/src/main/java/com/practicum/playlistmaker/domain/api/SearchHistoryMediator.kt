package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryMediator {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}