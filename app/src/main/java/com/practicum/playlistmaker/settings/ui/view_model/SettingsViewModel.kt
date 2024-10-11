package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.model.IntentAction
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsIteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _settingsLiveData = MutableLiveData<Boolean>()
    val settingsLiveData: LiveData<Boolean> get() = _settingsLiveData

    private val _sharingLiveData = MutableLiveData<IntentAction>()
    val sharingLiveData: LiveData<IntentAction> get() = _sharingLiveData

    init {
        if (getCurrentTheme().isNotBlank()) _settingsLiveData.value = settingsIteractor.getThemeSwitchState()
    }

    fun toggleSystemTheme(isChecked: Boolean) { _settingsLiveData.postValue(isChecked) }
    fun saveTheme(isChecked: Boolean) { settingsIteractor.updateThemeSetting(isChecked) }
    fun getCurrentTheme() = settingsIteractor.getThemeSettings().themeName

    private fun setShareAction(action: IntentAction) { _sharingLiveData.postValue(action) }
    fun shareApp() { setShareAction(sharingInteractor.getShareApp()) }
    fun contactSupport() { setShareAction(sharingInteractor.getContactSupport()) }
    fun openTerms() { setShareAction(sharingInteractor.getOpenTerms()) }
}