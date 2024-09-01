package com.practicum.playlistmaker.settings.data.api

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun getThemeSwitchState(): Boolean
    fun updateThemeSetting(isChecked: Boolean)
}