package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository,
) : SearchHistoryInteractor {

    override fun getHistory() = searchHistoryRepository.getHistory()

    override fun addTrack(track: Track) { searchHistoryRepository.addTrack(track) }

    override fun clearHistory() { searchHistoryRepository.clearHistory() }
}