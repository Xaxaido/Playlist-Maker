package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.common.Event
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.model.IntentAction
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsIteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _sharingLiveData = MutableLiveData<Event<IntentAction>>()
    val sharingLiveData: LiveData<Event<IntentAction>> get() = _sharingLiveData

    fun saveTheme(isChecked: Boolean) { settingsIteractor.updateThemeSetting(isChecked) }
    fun getCurrentTheme() = settingsIteractor.getThemeSettings().themeName
    fun getThemeSwitchState() = settingsIteractor.getThemeSwitchState()

    private fun setShareAction(action: IntentAction) { _sharingLiveData.postValue(Event(action)) }
    fun shareApp() { setShareAction(sharingInteractor.getShareApp()) }
    fun contactSupport() { setShareAction(sharingInteractor.getContactSupport()) }
    fun openTerms() { setShareAction(sharingInteractor.getOpenTerms()) }
}