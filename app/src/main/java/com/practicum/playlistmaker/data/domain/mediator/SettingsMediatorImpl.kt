package com.practicum.playlistmaker.data.domain.mediator

import com.practicum.playlistmaker.data.domain.repository.SettingsRepository

class SettingsMediatorImpl(
    private val settingsRepository: SettingsRepository
): SettingsMediator {

    override fun getThemeSettings() = settingsRepository.getThemeSettings()
    override fun updateThemeSetting(isChecked: Boolean) {
        settingsRepository.updateThemeSetting(isChecked)
    }
}