package com.practicum.playlistmaker.data.domain.repository

import com.practicum.playlistmaker.data.domain.model.Track

interface PrefsStorageRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun saveHistory(history: List<Track>)
    fun clearHistory()
}