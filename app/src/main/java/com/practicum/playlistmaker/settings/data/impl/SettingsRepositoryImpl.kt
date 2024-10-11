package com.practicum.playlistmaker.settings.data.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.AppTheme
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    context: Context,
    private val prefs: SharedPreferences,
) : SettingsRepository {

    private val key = context.getString(R.string.theme_dark)

    private fun getThemeName(isDarkThemeEnabled: Boolean) =
        if (isDarkThemeEnabled) {
            AppTheme.DARK.value
        } else {
            AppTheme.LIGHT.value
        }

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(prefs.getString(key, "") ?: "")
    }

    override fun getThemeSwitchState() = getThemeSettings().themeName == AppTheme.DARK.value

    override fun updateThemeSetting(isChecked: Boolean) {
        prefs.edit {
            putString(key, getThemeName(isChecked))
        }
    }
}