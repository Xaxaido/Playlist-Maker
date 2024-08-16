package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun getThemeSwitchState(): Boolean
    fun updateThemeSetting(isChecked: Boolean)
}