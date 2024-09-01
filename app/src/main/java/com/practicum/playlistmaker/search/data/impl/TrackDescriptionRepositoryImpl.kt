package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.search.data.source.SearchTrackDescriptionData
import com.practicum.playlistmaker.search.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.player.domain.model.TrackDescription

class TrackDescriptionRepositoryImpl(
    private val networkClient: NetworkClient,
    private val jsoup: SearchTrackDescriptionData,
) : TrackDescriptionRepository {

    override fun searchTrackDescription(term: String): TrackDescription {
        val response = networkClient.doRequest(SearchRequest(term))
        val result = if (response is TrackDescriptionSearchResponse) response.html else null

        return jsoup.parse(result)
    }
}