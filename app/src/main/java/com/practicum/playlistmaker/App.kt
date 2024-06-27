package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.practicum.playlistmaker.extension.util.Util

class App: Application() {

    val prefs: SharedPreferences by lazy { getSharedPreferences(getString(R.string.prefs_file_name), MODE_PRIVATE) }
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        darkTheme = prefs.getBoolean(getString(R.string.dark_mode_enabled), darkTheme)
        Util.toggleDarkTheme(darkTheme)
    }
}