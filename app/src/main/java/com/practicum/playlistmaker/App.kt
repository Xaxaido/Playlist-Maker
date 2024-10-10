package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.di.AppComponent
import com.practicum.playlistmaker.di.DaggerAppComponent
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import javax.inject.Inject

class App: Application() {

    lateinit var appComponent: AppComponent
    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()

        appComponent.inject(this)
        settingsInteractor.getThemeSettings().also { theme ->
            Util.applyTheme(theme.themeName)
        }
    }
}