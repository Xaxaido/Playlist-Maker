package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.common.resources.AppTheme
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.common.utils.Util

class App: Application() {

    var systemTheme = AppTheme.SYSTEM.value

    override fun onCreate() {
        super.onCreate()

        systemTheme = Creator.getSettingsInteractor(this).getThemeSettings().themeName
        Util.applyTheme(systemTheme)
    }
}