package com.practicum.playlistmaker.player.data.impl

import com.practicum.playlistmaker.search.data.dto.JsoupSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.search.data.source.SearchTrackDescriptionData
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.search.data.network.JsoupNetworkClient
import javax.inject.Inject

class TrackDescriptionRepositoryImpl @Inject constructor(
    private val networkClient: JsoupNetworkClient,
    private val jsoup: SearchTrackDescriptionData,
) : TrackDescriptionRepository {

    override fun searchTrackDescription(term: String): TrackDescription {
        val response = networkClient.doRequest(JsoupSearchRequest(term))
        val result = if (response is TrackDescriptionSearchResponse) response.html else null

        return jsoup.parse(result)
    }
}