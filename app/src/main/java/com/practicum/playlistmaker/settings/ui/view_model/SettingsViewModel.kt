package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.creator.Creator

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val settingsIteractor = Creator.getSettingsInteractor(getApplication())
    private val sharingInteractor = Creator.getSharingInteractor(getApplication())
    private val _liveData = MutableLiveData<Boolean>()
    val liveData: LiveData<Boolean> get() = _liveData

    init {
        _liveData.value = settingsIteractor.getThemeSwitchState()
    }

    fun shareApp() { sharingInteractor.shareApp() }
    fun contactSupport() { sharingInteractor.contactSupport() }
    fun openTerms() { sharingInteractor.openTerms() }

    fun toggleSystemTheme(isChecked: Boolean) {
        _liveData.value = isChecked
        settingsIteractor.updateThemeSetting(isChecked)
        Util.applyTheme(settingsIteractor.getThemeSettings().themeName)
    }

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    this[APPLICATION_KEY] as Application
                )
            }
        }
    }
}