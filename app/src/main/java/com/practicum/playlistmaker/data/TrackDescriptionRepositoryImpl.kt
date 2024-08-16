package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.SearchRequest
import com.practicum.playlistmaker.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.domain.TrackDescriptionSearchState
import com.practicum.playlistmaker.domain.api.JsoupRepository
import com.practicum.playlistmaker.domain.api.TrackDescriptionRepository

class TrackDescriptionRepositoryImpl(
    private val networkClient: NetworkClient,
    private val jsoup: JsoupRepository,
) : TrackDescriptionRepository {

    override fun searchTrackDescription(term: String): TrackDescriptionSearchState {
        val response = networkClient.doRequest(SearchRequest(term))

        return when (response.resultCode) {
            200 -> {
                val result = (response as TrackDescriptionSearchResponse).html

                if (result.isEmpty()) {
                    TrackDescriptionSearchState.Success(null)
                } else {
                    TrackDescriptionSearchState.Success(jsoup.parse(result))
                }
            }
            else -> TrackDescriptionSearchState.Error(error = response.resultCode)
        }
    }
}