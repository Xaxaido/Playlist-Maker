package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository,
) : SearchHistoryInteractor {

    override fun getHistory() = searchHistoryRepository.getHistory()

    override fun addTrack(track: Track) { searchHistoryRepository.addTrack(track) }

    override fun clearHistory() { searchHistoryRepository.clearHistory() }
}