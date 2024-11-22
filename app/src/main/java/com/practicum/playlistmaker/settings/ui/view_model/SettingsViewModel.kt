package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.model.IntentAction
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsIteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _sharedFlow = MutableSharedFlow<IntentAction>()
    val sharedFlow: SharedFlow<IntentAction> = _sharedFlow

    fun saveTheme(isChecked: Boolean) { settingsIteractor.updateThemeSetting(isChecked) }
    fun getCurrentTheme() = settingsIteractor.getThemeSettings().themeName
    fun getThemeSwitchState() = settingsIteractor.getThemeSwitchState()
    fun shareApp() { setShareAction(sharingInteractor.getShareApp()) }
    fun contactSupport() { setShareAction(sharingInteractor.getContactSupport()) }
    fun openTerms() { setShareAction(sharingInteractor.getOpenTerms()) }

    private fun setShareAction(action: IntentAction) {
        viewModelScope.launch {
            _sharedFlow.emit(action)
        }
    }
}