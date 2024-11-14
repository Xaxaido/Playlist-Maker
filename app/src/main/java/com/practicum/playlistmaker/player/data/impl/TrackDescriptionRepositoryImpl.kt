package com.practicum.playlistmaker.player.data.impl

import com.practicum.playlistmaker.search.data.dto.JsoupSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.search.data.source.SearchTrackDescriptionData
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.search.data.network.JsoupNetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackDescriptionRepositoryImpl(
    private val networkClient: JsoupNetworkClient,
    private val jsoup: SearchTrackDescriptionData,
) : TrackDescriptionRepository {

    override fun searchTrackDescription(term: String): Flow<TrackDescription> = flow {
        val response = networkClient.doRequest(JsoupSearchRequest(term))
        val result = if (response is TrackDescriptionSearchResponse) response.html else null
        emit(jsoup.parse(result))
    }
}