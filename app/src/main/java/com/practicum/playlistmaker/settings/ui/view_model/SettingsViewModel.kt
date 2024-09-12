package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.data.model.ShareAction
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsIteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _settingsLiveData = MutableLiveData<Boolean>()
    val settingsLiveData: LiveData<Boolean> get() = _settingsLiveData

    private val _sharingLiveData = MutableLiveData<ShareAction>()
    val sharingLiveData: LiveData<ShareAction> get() = _sharingLiveData

    init {
        _settingsLiveData.value = settingsIteractor.getThemeSwitchState()
    }

    fun toggleSystemTheme(isChecked: Boolean) { _settingsLiveData.postValue(isChecked) }
    fun saveTheme(isChecked: Boolean) { settingsIteractor.updateThemeSetting(isChecked) }
    fun getCurrentTheme() = settingsIteractor.getThemeSettings().themeName

    private fun setShareAction(action: ShareAction) { _sharingLiveData.postValue(action) }
    fun shareApp() { setShareAction(sharingInteractor.getShareApp()) }
    fun contactSupport() { setShareAction(sharingInteractor.getContactSupport()) }
    fun openTerms() { setShareAction(sharingInteractor.getOpenTerms()) }
}