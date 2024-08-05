package com.practicum.playlistmaker.data.domain.repository

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ThemeSettings

class SettingsRepositoryImpl(
    private val context: Context,
    private val prefs: SharedPreferences,
) : SettingsRepository {

    private val key = context.getString(R.string.theme_auto)

    override fun getThemeSettings() = ThemeSettings(context, prefs.getBoolean(key, true))

    override fun updateThemeSetting(isChecked: Boolean) {
        prefs.edit()
            .putBoolean(key, isChecked)
            .apply()
    }
}