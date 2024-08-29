package com.practicum.playlistmaker.settings.domain.api

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun getThemeSwitchState(): Boolean
    fun updateThemeSetting(isChecked: Boolean)
}