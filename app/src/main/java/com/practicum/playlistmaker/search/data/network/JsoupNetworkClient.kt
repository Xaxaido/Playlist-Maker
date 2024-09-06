package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.common.utils.Util
import org.jsoup.Jsoup

class JsoupNetworkClient : NetworkClientBase {

    override fun doRequest(dto: Any): Response {
       return if (dto is SearchRequest) {

            try {
                val result = Jsoup.connect(dto.term).execute()
                val body = result.body()?.let {
                    TrackDescriptionSearchResponse(it)
                } ?: Response()

                body
            } catch (e: Throwable) {
                Response().apply { resultCode = Util.HTTP_NOT_FOUND }
            }
        } else {
            Response().apply { resultCode = Util.HTTP_BAD_REQUEST }
        }
    }
}