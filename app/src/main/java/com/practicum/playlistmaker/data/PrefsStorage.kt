package com.practicum.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.entity.Track

private const val HISTORY_MAX_COUNT = 10

class PrefsStorage(
    context: Context,
    private val prefs: SharedPreferences
) {

    private val key = context.getString(R.string.search_history)

    fun getHistory(): List<Track> = run {
        val prefsHistory = prefs.getString(key, null)

        if (!prefsHistory.isNullOrBlank()) {
            Gson().fromJson(prefsHistory, object : TypeToken<List<Track>>() {}.type) ?: emptyList()
        } else emptyList()
    }

    fun addTrack(track: Track) {
        with (getHistory().toMutableList()) {
            indexOfFirst { it.trackId == track.trackId }.apply {
                if (this != -1) removeAt(this)
            }
            add(0, track)
            if (size > HISTORY_MAX_COUNT) removeLast()
            saveHistory(this)
        }
    }

    fun clearHistory() { saveHistory(emptyList()) }

    private fun saveHistory(history: List<Track>) {
        prefs.edit().putString(key, Gson().toJson(history)).apply()
    }
}