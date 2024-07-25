package com.practicum.playlistmaker.extension.util

import android.content.Context
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

object Util {

    const val KEY_TRACK = "key_track"

    fun String.toDate() = SimpleDateFormat("yyyy", Locale.getDefault())
        .parse(this)
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.year
        ?.toString()
        ?: ""

    fun Long.millisToSeconds() = SimpleDateFormat(
        "mm : ss",
        Locale.getDefault()
    ).format(this) ?: ""

    fun Int.dpToPx(context: Context) = (this * context.resources.displayMetrics.density).toInt()
}