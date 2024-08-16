package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.TrackDescription

interface JsoupRepository {
    fun parse(html: String): TrackDescription
}