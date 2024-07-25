package com.practicum.playlistmaker.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.data.model.entity.Track

private const val HISTORY_MAX_COUNT = 10

class PrefsMediator(
    context: Context,
) {

    private val prefs = context as App

    fun getHistory(): List<Track> = run {
        val history = prefs.getHistory()

        if (history.isNotBlank()) {
            Gson().fromJson(history, object : TypeToken<List<Track>>() {}.type) ?: emptyList()
        } else emptyList()
    }

    fun addTrack(track: Track) {
        with (getHistory().toMutableList()) {
            removeIf { it.trackId == track.trackId }
            add(0, track)
            if (size > HISTORY_MAX_COUNT) removeLast()
            saveHistory(this)
        }
    }

    fun clearHistory() { saveHistory(emptyList()) }

    private fun saveHistory(history: List<Track>) {
        prefs.saveHistory(Gson().toJson(history))
    }
}