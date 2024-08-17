package com.practicum.playlistmaker.presentation.settings

import com.practicum.playlistmaker.common.Util
import com.practicum.playlistmaker.domain.api.ExternalNavigator
import com.practicum.playlistmaker.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsPresenter(
    externalNavigator: ExternalNavigator,
    private val settingsRepository: SettingsRepository,
) {

    private val sharingMediator = SharingInteractorImpl(externalNavigator)

    fun getTheme() = settingsRepository.getThemeSettings().themeName
    fun getThemeSwitchState() = settingsRepository.getThemeSwitchState()
    fun shareApp() { sharingMediator.shareApp() }
    fun contactSupport() { sharingMediator.contactSupport() }
    fun userAgreement() { sharingMediator.userAgreement() }

    fun toggleSystemTheme(isChecked: Boolean) {
        settingsRepository.updateThemeSetting(isChecked)
        Util.applyTheme(settingsRepository.getThemeSettings().themeName)
    }
}