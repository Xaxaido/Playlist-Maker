package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import javax.inject.Inject

class SearchHistoryInteractorImpl @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository,
) : SearchHistoryInteractor {

    override val history = searchHistoryRepository.history

    override fun addTrack(track: Track) { searchHistoryRepository.addTrack(track) }

    override fun removeTrack(pos: Int) { searchHistoryRepository.removeTrack(pos) }

    override fun clearHistory() { searchHistoryRepository.clearHistory() }
}