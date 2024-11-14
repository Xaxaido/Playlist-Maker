package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.JsoupSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.data.network.JsoupNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class JsoupNetworkClientImpl : JsoupNetworkClient {

    override suspend fun doRequest(dto: JsoupSearchRequest): Response {
        return withContext(Dispatchers.IO) {
            try {
                val result = Jsoup.connect(dto.term).execute()
                result.body()?.let {
                    TrackDescriptionSearchResponse(it)
                } ?: Response()
            } catch (e: Exception) {
                Response().apply {
                    resultCode = Util.HTTP_NOT_FOUND
                }
            }
        }
    }
}