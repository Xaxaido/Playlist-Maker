package com.practicum.playlistmaker.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.entity.Track

private const val HISTORY_MAX_COUNT = 10

class PrefsStorage(
    private val context: Context,
) {

    private val key = context.getString(R.string.search_history)

    fun getHistory(): List<Track> = run {
        val prefsHistory = (context as App).getString(key)

        if (prefsHistory.isNotBlank()) {
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
        (context as App).putString(key, Gson().toJson(history))
    }
}