package com.practicum.playlistmaker.presentation.settings

import com.practicum.playlistmaker.common.Util
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SharingInteractor

class SettingsPresenter(
    private val settingsIteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) {

    fun getTheme() = settingsIteractor.getThemeSettings().themeName
    fun getThemeSwitchState() = settingsIteractor.getThemeSwitchState()
    fun shareApp() { sharingInteractor.shareApp() }
    fun contactSupport() { sharingInteractor.contactSupport() }
    fun userAgreement() { sharingInteractor.userAgreement() }

    fun toggleSystemTheme(isChecked: Boolean) {
        settingsIteractor.updateThemeSetting(isChecked)
        Util.applyTheme(settingsIteractor.getThemeSettings().themeName)
    }
}