package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.Prefs

class App: Application() {

    var darkTheme = false
    private val prefs = Prefs(this)

    override fun onCreate() {
        super.onCreate()

        darkTheme = prefs.getBoolean(getString(R.string.dark_mode_enabled))
        toggleDarkTheme(darkTheme)
    }

    fun getHistory() = prefs.getString(getString(R.string.search_history))

    fun saveHistory(history: String) {
        prefs.putString(getString(R.string.search_history), history)
    }

    fun saveDarkThemeState(isEnabled: Boolean) {
        prefs.putBoolean(getString(R.string.dark_mode_enabled), isEnabled)
    }

    fun toggleDarkTheme(isEnabled: Boolean) {
        darkTheme = isEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}