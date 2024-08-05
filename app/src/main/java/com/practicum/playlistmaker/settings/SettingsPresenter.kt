package com.practicum.playlistmaker.settings

import com.practicum.playlistmaker.data.domain.mediator.SettingsMediatorImpl
import com.practicum.playlistmaker.data.domain.mediator.SharingMediatorImpl
import com.practicum.playlistmaker.data.domain.repository.SettingsRepository
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