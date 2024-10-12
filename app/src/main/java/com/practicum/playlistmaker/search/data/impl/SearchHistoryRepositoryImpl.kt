package com.practicum.playlistmaker.search.data.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    context: Context,
    private val prefs: SharedPreferences,
    private val gson: Gson,
) : SearchHistoryRepository {

    private val key = context.getString(R.string.search_history)
    private val _history by lazy { getSearchHistory() }
    override val history: List<Track> get() = _history

    private fun getSearchHistory(): MutableList<Track> {
        val prefsHistory = prefs.getString(key, null)

        return if (!prefsHistory.isNullOrBlank()) {
            gson.fromJson(prefsHistory, object : TypeToken<List<Track>>() {}.type) ?: mutableListOf()
        } else mutableListOf()
    }

    override fun addTrack(track: Track) {
        _history.apply {
            removeIf { it.id == track.id }
            add(0, track)
            if (size > Util.HISTORY_MAX_COUNT) removeLast()
        }
        saveHistory()
    }

    override fun removeTrack(pos: Int) {
        _history.removeAt(pos)
        saveHistory()
    }

    override fun clearHistory() {
        _history.clear()
        saveHistory()
    }

    private fun saveHistory() {
        prefs.edit {
            putString(key, gson.toJson(_history))
        }
    }
}