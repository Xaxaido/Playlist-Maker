package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository,
): SettingsInteractor {

    override fun getThemeSettings() = settingsRepository.getThemeSettings()
    override fun getThemeSwitchState() = settingsRepository.getThemeSwitchState()
    override fun updateThemeSetting(isChecked: Boolean) = settingsRepository.updateThemeSetting(isChecked)
}