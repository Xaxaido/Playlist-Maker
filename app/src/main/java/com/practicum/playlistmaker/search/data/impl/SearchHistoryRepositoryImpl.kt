package com.practicum.playlistmaker.search.data.impl

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    context: Context,
    private val prefs: SharedPreferences,
) : SearchHistoryRepository {

    private val key = context.getString(R.string.search_history)
    private val _history get() = getHistory().toMutableList()

    override fun getHistory(): List<Track> = run {
        val prefsHistory = prefs.getString(key, null)

        if (!prefsHistory.isNullOrBlank()) {
            Gson().fromJson(prefsHistory, object : TypeToken<List<Track>>() {}.type) ?: emptyList()
        } else emptyList()
    }

    override fun addTrack(track: Track) {
        _history.apply {
            removeIf { it.id == track.id }
            add(0, track)
            if (size > Util.HISTORY_MAX_COUNT) removeLast()
            saveHistory(this)
        }
    }

    override fun removeTrack(pos: Int) {
        _history.apply {
            removeAt(pos)
            saveHistory(this)
        }
    }

    override fun clearHistory() { saveHistory(emptyList()) }

    private fun saveHistory(history: List<Track>) {
        prefs.edit().putString(key, Gson().toJson(history)).apply()
    }
}