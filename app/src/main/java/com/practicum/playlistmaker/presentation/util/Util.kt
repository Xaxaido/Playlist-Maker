package com.practicum.playlistmaker.presentation.util

import android.content.Context
import android.util.TypedValue
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.resources.AppTheme
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

object Util {

    const val KEY_TRACK = "KEY_TRACK"
    const val USER_INPUT_DELAY = 2000L
    const val BUTTON_ENABLED_DELAY = 1000L
    const val PLAYER_ALBUM_COVER_WIDTH_MULTIPLIER = 1
    const val HTTP_NOT_FOUND = 404
    const val HTTP_BAD_REQUEST = 400

    fun Long.millisToSeconds() = SimpleDateFormat(
        "mm : ss",
        Locale.getDefault()
    ).format(this) ?: ""

    fun String.toDate() = SimpleDateFormat("yyyy", Locale.getDefault())
        .parse(this)
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.year
        ?.toString()
        ?: ""

    fun Number.dpToPx(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()

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