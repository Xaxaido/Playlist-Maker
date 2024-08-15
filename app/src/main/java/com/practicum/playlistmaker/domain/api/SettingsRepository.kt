package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.presentation.settings.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(isChecked: Boolean)
}