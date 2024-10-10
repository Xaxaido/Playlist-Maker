package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.RetrofitSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import retrofit2.Call
import java.io.IOException

class RetrofitNetworkClientImpl(
    private val iTunesService: ITunesService,
) : RetrofitNetworkClient {

    private var call: Call<TracksSearchResponse>? = null

    override fun doRequest(dto: RetrofitSearchRequest): Response {
        return try {
            call = iTunesService.searchTracks(term = dto.term, offset = dto.page)
            val result = call?.execute()

            result?.body()?.apply { resultCode = result.code()
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