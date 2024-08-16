package com.practicum.playlistmaker.data

import android.app.Application
import com.practicum.playlistmaker.presentation.util.Util

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        Util.applyTheme(Creator.getSettingsPresenter(this).getTheme())
    }
}