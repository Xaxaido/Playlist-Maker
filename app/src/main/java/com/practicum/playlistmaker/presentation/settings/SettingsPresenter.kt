package com.practicum.playlistmaker.presentation.settings

import com.practicum.playlistmaker.domain.impl.SettingsMediatorImpl
import com.practicum.playlistmaker.domain.impl.SharingMediatorImpl
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.extension.util.Util

class SettingsPresenter(
    externalNavigator: ExternalNavigator,
    settingsRepository: SettingsRepository,
) {

    private val sharingMediator = SharingMediatorImpl(externalNavigator)
    private val settingsMediator = SettingsMediatorImpl(settingsRepository)

    fun getTheme() = settingsMediator.getThemeSettings().appTheme
    fun shareApp() { sharingMediator.shareApp() }
    fun contactSupport() { sharingMediator.contactSupport() }
    fun userAgreement() { sharingMediator.userAgreement() }

    fun toggleSystemTheme(isChecked: Boolean) {
        settingsMediator.updateThemeSetting(isChecked)
        Util.applyTheme(settingsMediator.getThemeSettings().appTheme)
    }
}