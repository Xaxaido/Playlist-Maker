package com.practicum.playlistmaker.data.domain.repository

import com.practicum.playlistmaker.settings.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(isChecked: Boolean)
}