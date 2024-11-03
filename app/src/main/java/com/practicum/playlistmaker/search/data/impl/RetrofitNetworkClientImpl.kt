package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.RetrofitSearchRequest
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class RetrofitNetworkClientImpl(
    private val iTunesService: ITunesService,
) : RetrofitNetworkClient {

    override suspend fun doRequest(dto: RetrofitSearchRequest): Response {
        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.searchTracks(term = dto.term, offset = dto.page)
                response.apply {
                    resultCode = Util.HTTP_OK
                }
            } catch (e: IOException) {
                Response().apply {
                    resultCode = when (e.message) {
                        "Canceled" -> Util.REQUEST_CANCELLED
                        else -> Util.REQUEST_TIMEOUT
                    }
                }
            }
        }
    }
}