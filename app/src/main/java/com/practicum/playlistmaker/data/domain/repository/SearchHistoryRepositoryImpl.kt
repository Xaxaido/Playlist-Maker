package com.practicum.playlistmaker.data.domain.repository

import com.practicum.playlistmaker.data.domain.model.Track

class SearchHistoryRepositoryImpl(
    private val prefsStorage: PrefsStorageRepository
) : SearchHistoryRepository {

    override fun getHistory() = prefsStorage.getHistory()

    override fun addTrack(track: Track) { prefsStorage.addTrack(track) }

    override fun clearHistory() { prefsStorage.clearHistory() }
}