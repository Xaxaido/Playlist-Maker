package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface PrefsStorageRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun saveHistory(history: List<Track>)
    fun clearHistory()
}