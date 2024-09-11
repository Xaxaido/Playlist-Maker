package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.common.utils.DtoConverter.toTrack
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.domain.api.TracksRepository

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
) : TracksRepository {

    override fun searchTracks(term: String): TracksSearchState {
        val response = networkClient.doRequest(SearchRequest(term))

        return when (response.resultCode) {
            Util.NO_CONNECTION -> {
                TracksSearchState.Error(error = Util.NO_CONNECTION)
            }
            Util.HTTP_OK -> {
                val result = (response as TracksSearchResponse).results

                if (result.isEmpty()) {
                    TracksSearchState.Success(emptyList())
                } else {
                    val items = result.filter { !it.previewUrl.isNullOrEmpty() }
                    TracksSearchState.Success(items.map { it.toTrack() })
                }
            }
            else -> {
                TracksSearchState.Error(error = response.resultCode)
            }
        }
    }

    override fun cancelRequest() { networkClient.cancelRequest() }
}