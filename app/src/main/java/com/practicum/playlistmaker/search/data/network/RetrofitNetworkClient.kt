package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import java.io.IOException

class RetrofitNetworkClient(
    private val iTunesService: ITunesService,
) : NetworkClient {

    private var call: Call<TracksSearchResponse>? = null

    override fun doRequest(dto: SearchRequest): Response {
        return try {
            call = iTunesService.searchTracks(dto.term)
            val result = call?.execute()

            result?.body()?.apply {
                resultCode = result.code()
            } ?: Response()
        } catch (e: IOException) {
            Response().apply {
                resultCode = when (e.message) {
                    "Canceled" -> Util.REQUEST_CANCELLED
                    else -> Util.REQUEST_TIMEOUT
                }
            }
        }
    }

    override fun cancelRequest() { call?.cancel() }
}