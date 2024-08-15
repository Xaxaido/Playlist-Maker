package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.SettingsMediator

class SettingsMediatorImpl(
    private val settingsRepository: SettingsRepository
): SettingsMediator {

    override fun getThemeSettings() = settingsRepository.getThemeSettings()
    override fun updateThemeSetting(isChecked: Boolean) {
        settingsRepository.updateThemeSetting(isChecked)
    }
}