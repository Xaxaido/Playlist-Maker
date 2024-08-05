package com.practicum.playlistmaker.data.domain.mediator

import com.practicum.playlistmaker.data.domain.model.Track
import com.practicum.playlistmaker.data.domain.repository.SearchHistoryRepository

class SearchHistoryMediatorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryMediator {

    override fun getHistory() = searchHistoryRepository.getHistory()

    override fun addTrack(track: Track) { searchHistoryRepository.addTrack(track) }

    override fun clearHistory() { searchHistoryRepository.clearHistory() }
}