package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import javax.inject.Inject

class SettingsInteractorImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
): SettingsInteractor {

    override fun getThemeSettings() = settingsRepository.getThemeSettings()
    override fun getThemeSwitchState() = settingsRepository.getThemeSwitchState()
    override fun updateThemeSetting(isChecked: Boolean) = settingsRepository.updateThemeSetting(isChecked)
}