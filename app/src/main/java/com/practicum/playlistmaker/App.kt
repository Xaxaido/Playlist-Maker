package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        settingsInteractor.getThemeSettings().also { theme ->
            Util.applyTheme(theme.themeName)
        }
    }
}