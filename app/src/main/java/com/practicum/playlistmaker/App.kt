package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule,
            )
        }

        val settingsInteractor: SettingsInteractor by inject()
        settingsInteractor.getThemeSettings().also { theme ->
            Util.applyTheme(theme.themeName)
        }
    }
}