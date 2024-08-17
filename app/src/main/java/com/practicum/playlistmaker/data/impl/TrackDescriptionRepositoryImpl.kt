package com.practicum.playlistmaker.data.impl

import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.dto.SearchRequest
import com.practicum.playlistmaker.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.domain.api.JsoupRepository
import com.practicum.playlistmaker.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.domain.models.TrackDescription

class TrackDescriptionRepositoryImpl(
    private val networkClient: NetworkClient,
    private val jsoup: JsoupRepository,
) : TrackDescriptionRepository {

    override fun searchTrackDescription(term: String): TrackDescription {
        val response = networkClient.doRequest(SearchRequest(term))
        val result = if (response is TrackDescriptionSearchResponse) response.html else null

        return jsoup.parse(result)
    }
}