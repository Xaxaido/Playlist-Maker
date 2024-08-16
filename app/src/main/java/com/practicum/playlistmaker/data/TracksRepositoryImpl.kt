package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.SearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.TracksSearchState
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.data.DtoConverter.toTrack

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
) : TracksRepository {

    override fun searchTracks(term: String): TracksSearchState {
        val response = networkClient.doRequest(SearchRequest(term))

        return when (response.resultCode) {
            200 -> {
                val result = (response as TracksSearchResponse).results

                if (result.isEmpty()) {
                    TracksSearchState.Success(emptyList())
                } else {
                    TracksSearchState.Success(result.map { it.toTrack() })
                }
            }
            else -> TracksSearchState.Error(error = response.resultCode)
        }
    }
}