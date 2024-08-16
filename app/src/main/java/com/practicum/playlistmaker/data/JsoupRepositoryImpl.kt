package com.practicum.playlistmaker.data

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.JsoupRepository
import com.practicum.playlistmaker.domain.models.TrackDescription
import org.jsoup.Jsoup

private const val COUNTRY_CSS_SELECTOR = "dd[data-testid=grouptext-section-content]"

class JsoupRepositoryImpl(
    private val context: Context,
) : JsoupRepository {

    override fun parse(html: String): TrackDescription {
        val document = Jsoup.parse(html)
        val element = document.select(COUNTRY_CSS_SELECTOR).firstOrNull()
        val country = element?.text()?.substringAfter(",") ?: context.getString(R.string.player_unknown)

        return TrackDescription(country)
    }
}