package com.practicum.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.PrefsStorageRepository
import com.practicum.playlistmaker.domain.models.Track

private const val HISTORY_MAX_COUNT = 10

class PrefsStorageRepositoryImpl(
    context: Context,
    private val prefs: SharedPreferences,
) : PrefsStorageRepository {

    private val key = context.getString(R.string.search_history)

    override fun getHistory(): List<Track> = run {
        val prefsHistory = prefs.getString(key, null)

        if (!prefsHistory.isNullOrBlank()) {
            Gson().fromJson(prefsHistory, object : TypeToken<List<Track>>() {}.type) ?: emptyList()
        } else emptyList()
    }


    override fun addTrack(track: Track) {
        with (getHistory().toMutableList()) {
            removeIf { it.trackId == track.trackId }
            add(0, track)
            if (size > HISTORY_MAX_COUNT) removeLast()
            saveHistory(this)
        }
    }

    override fun clearHistory() { saveHistory(emptyList()) }

    override fun saveHistory(history: List<Track>) {
        prefs.edit().putString(key, Gson().toJson(history)).apply()
    }
}