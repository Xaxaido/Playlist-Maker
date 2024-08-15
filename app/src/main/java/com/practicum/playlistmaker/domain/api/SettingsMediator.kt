package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.resources.ThemeSettings

interface SettingsMediator {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(isChecked: Boolean)
}