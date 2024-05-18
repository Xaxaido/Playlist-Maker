package com.practicum.playlistmaker.extension.utils

import androidx.appcompat.app.AppCompatDelegate

class Utils {

    companion object {

        fun toggleDarkTheme(isEnabled: Boolean) {
            AppCompatDelegate.setDefaultNightMode(
                if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}