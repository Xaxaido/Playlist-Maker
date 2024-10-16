package com.practicum.playlistmaker.search.data.impl

import com.google.gson.Gson
import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.common.utils.DtoConverter.toTracksList
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.data.dto.RetrofitSearchRequest
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Track

class TracksRepositoryImpl(
    private val networkClient: RetrofitNetworkClient,
    private val gson: Gson,
) : TracksRepository {

    override fun trackToJson(track: Track): String = gson.toJson(track)

    override fun searchTracks(term: String, page: Int): TracksSearchState {
        val response = networkClient.doRequest(RetrofitSearchRequest(term, page))

        return when (response.resultCode) {
            Util.NO_CONNECTION -> {
                TracksSearchState.Error(error = Util.NO_CONNECTION)
            }
            Util.HTTP_OK -> {
                val result = (response as TracksSearchResponse).results

                if (result.isEmpty()) {
                    TracksSearchState.Success(emptyList())
                } else {
                    result.filter { !it.previewUrl.isNullOrEmpty() }.let {
                        TracksSearchState.Success(it.toTracksList())
                    }
                }
            }
            else -> {
                TracksSearchState.Error(error = response.resultCode)
            }
        }
    }

    override fun cancelRequest() { networkClient.cancelRequest() }
}