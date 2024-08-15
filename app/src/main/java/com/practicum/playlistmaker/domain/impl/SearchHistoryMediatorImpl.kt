package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryMediator

class SearchHistoryMediatorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryMediator {

    override fun getHistory() = searchHistoryRepository.getHistory()

    override fun addTrack(track: Track) { searchHistoryRepository.addTrack(track) }

    override fun clearHistory() { searchHistoryRepository.clearHistory() }
}