package com.practicum.playlistmaker.common.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.practicum.playlistmaker.common.resources.AppTheme
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx

object Util {

    const val COUNTRY_CSS_SELECTOR = "dd[data-testid=grouptext-section-content]"
    const val ANIMATION_SHORT= 250L
    const val UPDATE_PLAYBACK_PROGRESS_DELAY= 300L
    const val BUTTON_ENABLED_DELAY = 1000L
    const val USER_INPUT_DELAY = 2000L
    const val PARTICLE_DURATION = 2000L
    const val HISTORY_MAX_COUNT = 10
    const val NO_CONNECTION = -1
    const val HTTP_OK = 200
    const val HTTP_BAD_REQUEST = 400
    const val HTTP_NOT_FOUND = 404
    const val REQUEST_TIMEOUT = 408
    const val REQUEST_CANCELLED = -2
    const val ICON_SIZE = 32
    const val WIND_EFFECT = 1f
    const val PARTICLE_SIZE = 1.5f
    const val UNDERLAY_BUTTON_TEXT_SIZE = 30f
    const val UNDERLAY_BUTTON_WIDTH = 200

    fun applyTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                AppTheme.LIGHT.value -> AppCompatDelegate.MODE_NIGHT_NO
                AppTheme.DARK.value -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun drawableToBitmap(context: Context, image: Drawable?) =
        image?.toBitmap()?.scale(
            ICON_SIZE.dpToPx(context),
            ICON_SIZE.dpToPx(context),
            false
        )
}