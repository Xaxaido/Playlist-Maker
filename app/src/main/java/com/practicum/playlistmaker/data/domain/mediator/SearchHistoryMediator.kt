package com.practicum.playlistmaker.data.domain.mediator

import com.practicum.playlistmaker.data.domain.model.Track

interface SearchHistoryMediator {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}