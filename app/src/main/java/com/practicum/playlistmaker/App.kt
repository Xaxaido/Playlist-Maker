package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.common.utils.Util

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        val systemTheme = Creator.getSettingsInteractor(this).getThemeSettings().themeName
        Util.applyTheme(systemTheme)
    }
}