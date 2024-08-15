package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.impl.SettingsMediatorImpl
import com.practicum.playlistmaker.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.extension.util.Util

class App: Application() {

    val prefs: SharedPreferences by lazy {
        getSharedPreferences(getString(R.string.prefs_file_name), MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()

        val settingsMediator =  SettingsMediatorImpl(
            SettingsRepositoryImpl(applicationContext, prefs)
        )

        val appTheme = settingsMediator.getThemeSettings().appTheme
        Util.applyTheme(appTheme)
    }
}