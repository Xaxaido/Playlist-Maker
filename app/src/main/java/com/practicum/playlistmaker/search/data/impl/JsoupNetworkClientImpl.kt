package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.JsoupSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.data.network.JsoupNetworkClient
import org.jsoup.Jsoup

class JsoupNetworkClientImpl : JsoupNetworkClient {

    override fun doRequest(dto: JsoupSearchRequest): Response {
        return try {
            val result = Jsoup.connect(dto.term).execute()
            val body = result.body()?.let {
                TrackDescriptionSearchResponse(it)
            } ?: Response()

            body
        } catch (e: Throwable) {
            Response().apply { resultCode = Util.HTTP_NOT_FOUND }
        }
    }
}