package com.practicum.playlistmaker.common.utils

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

object Extensions {

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

    fun Long.millisToSeconds() = SimpleDateFormat(
        "mm : ss",
        Locale.getDefault()
    ).format(this) ?: ""
}