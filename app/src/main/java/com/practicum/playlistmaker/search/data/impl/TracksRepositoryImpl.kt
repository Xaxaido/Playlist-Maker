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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: RetrofitNetworkClient,
    private val gson: Gson,
) : TracksRepository {

    override fun trackToJson(track: Track): String = gson.toJson(track)

    override fun searchTracks(term: String, page: Int): Flow<TracksSearchState> = flow {
        val response = networkClient.doRequest(RetrofitSearchRequest(term, page))

        when (response.resultCode) {
            Util.INTERNAL_SERVER_ERROR -> {
                emit(TracksSearchState.Error(Util.INTERNAL_SERVER_ERROR, term))
            }
            Util.HTTP_OK -> {
                val result = (response as TracksSearchResponse).results

                emit(
                    if (result.isEmpty()) {
                        TracksSearchState.Success(emptyList(), term)
                    } else {
                        result.filter { !it.previewUrl.isNullOrEmpty() }.let {
                            TracksSearchState.Success(it.toTracksList(), term)
                        }
                    }
                )
            }
            else -> {
                emit(TracksSearchState.Error(response.resultCode, term))
            }
        }
    }
}