package com.practicum.playlistmaker.data.source

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.utils.Extensions
import com.practicum.playlistmaker.domain.models.TrackDescription
import org.jsoup.Jsoup

class SearchTrackDescriptionData(
    context: Context,
) {

    private val nothingFound = context.getString(R.string.player_unknown)

    fun parse(html: String?): TrackDescription {
        return html?.let {
            val document = Jsoup.parse(html)
            val element = document.select(Extensions.COUNTRY_CSS_SELECTOR).firstOrNull()
            val country = element?.text()?.substringAfter(",") ?: nothingFound
            TrackDescription(country)
        } ?: TrackDescription(nothingFound)
    }
}