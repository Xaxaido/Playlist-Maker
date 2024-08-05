package com.practicum.playlistmaker.data.domain.repository

import com.practicum.playlistmaker.data.domain.model.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}