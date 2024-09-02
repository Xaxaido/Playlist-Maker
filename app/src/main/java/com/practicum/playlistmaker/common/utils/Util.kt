package com.practicum.playlistmaker.common.utils

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.common.resources.AppTheme

object Util {

    const val KEY_TRACK = "KEY_TRACK"
    const val USER_INPUT_DELAY = 2000L
    const val BUTTON_ENABLED_DELAY = 1000L
    const val ANIMATION_SHORT= 250L
    const val PLAYER_ALBUM_COVER_WIDTH_MULTIPLIER = 1
    const val HISTORY_MAX_COUNT = 10
    const val COUNTRY_CSS_SELECTOR = "dd[data-testid=grouptext-section-content]"
    const val UPDATE_PLAYBACK_PROGRESS = "UPDATE_PLAYBACK_PROGRESS"
    const val UPDATE_BUFFERED_PROGRESS = "UPDATE_BUFFERED_PROGRESS"
    const val NO_CONNECTION = -1
    const val HTTP_OK = 200
    const val HTTP_BAD_REQUEST = 400
    const val HTTP_NOT_FOUND = 404
    const val REQUEST_TIMEOUT = 408

    fun applyTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                AppTheme.LIGHT.value -> AppCompatDelegate.MODE_NIGHT_NO
                AppTheme.DARK.value -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}