package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun getThemeSwitchState(): Boolean
    fun updateThemeSetting(isChecked: Boolean)
}