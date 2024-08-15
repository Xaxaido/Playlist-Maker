package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.resources.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(isChecked: Boolean)
}