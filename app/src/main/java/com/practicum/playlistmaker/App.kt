package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.PrefsStorage
import com.practicum.playlistmaker.data.model.resources.AppTheme

class App: Application() {

    lateinit var appTheme: String
    private val prefs = PrefsStorage(this)

    override fun onCreate() {
        super.onCreate()

        appTheme = prefs.getString(getString(R.string.app_theme), AppTheme.SYSTEM.value)
        applyTheme(appTheme)
    }

    fun getHistory() = prefs.getString(getString(R.string.search_history))

    fun saveHistory(history: String) {
        prefs.putString(getString(R.string.search_history), history)
    }

    fun saveTheme(theme: String) {
        prefs.putString(getString(R.string.app_theme), theme)
    }

    fun applyTheme(theme: String) {
        appTheme = theme
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                AppTheme.LIGHT.value -> AppCompatDelegate.MODE_NIGHT_NO
                AppTheme.DARK.value -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}