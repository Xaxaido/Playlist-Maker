package com.practicum.playlistmaker.data.utils

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

object Extensions {

    const val HTTP_NOT_FOUND = 404
    const val HTTP_BAD_REQUEST = 400
    const val HISTORY_MAX_COUNT = 10
    const val COUNTRY_CSS_SELECTOR = "dd[data-testid=grouptext-section-content]"

    fun String.toDate() = SimpleDateFormat("yyyy", Locale.getDefault())
        .parse(this)
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.year
        ?.toString()
        ?: ""
}