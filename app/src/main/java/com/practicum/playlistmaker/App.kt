package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.common.utils.Util

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        Creator.init(applicationContext)
        Creator.getSettingsInteractor().getThemeSettings().also { theme ->
            Util.applyTheme(theme.themeName)
        }
    }
}