package com.practicum.playlistmaker.data.impl

import com.practicum.playlistmaker.domain.api.PrefsStorageRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val prefsStorage: PrefsStorageRepository,
) : SearchHistoryRepository {

    override fun getHistory() = prefsStorage.getHistory()

    override fun addTrack(track: Track) { prefsStorage.addTrack(track) }

    override fun clearHistory() { prefsStorage.clearHistory() }
}