package com.practicum.playlistmaker.data.domain.mediator

import com.practicum.playlistmaker.settings.ThemeSettings

interface SettingsMediator {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(isChecked: Boolean)
}