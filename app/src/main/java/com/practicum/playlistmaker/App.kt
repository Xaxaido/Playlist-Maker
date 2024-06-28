package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App: Application() {

    var darkTheme = false
    private val prefs: SharedPreferences by lazy {
        getSharedPreferences(getString(R.string.prefs_file_name), MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()

        darkTheme = prefs.getBoolean(getString(R.string.dark_mode_enabled), darkTheme)
        toggleDarkTheme(darkTheme)
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String): String {
        return prefs.getString(key, "") ?: ""
    }

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun toggleDarkTheme(isEnabled: Boolean) {
        darkTheme = isEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}