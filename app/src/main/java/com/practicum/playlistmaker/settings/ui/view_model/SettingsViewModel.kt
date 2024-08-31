package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsIteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _liveData = MutableLiveData<Boolean>()
    val liveData: LiveData<Boolean> get() = _liveData

    init {
        _liveData.value = settingsIteractor.getThemeSwitchState()
    }

    fun saveTheme(isChecked: Boolean) { settingsIteractor.updateThemeSetting(isChecked) }
    fun getCurrentTheme() = settingsIteractor.getThemeSettings().themeName

    fun shareApp() { sharingInteractor.shareApp() }
    fun contactSupport() { sharingInteractor.contactSupport() }
    fun openTerms() { sharingInteractor.openTerms() }

    fun toggleSystemTheme(isChecked: Boolean) {
        _liveData.value = isChecked
    }

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    Creator.getSettingsInteractor(),
                    Creator.getSharingInteractor(),
                )
            }
        }
    }
}