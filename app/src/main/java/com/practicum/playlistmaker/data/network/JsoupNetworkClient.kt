package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.SearchRequest
import com.practicum.playlistmaker.data.dto.TrackDescriptionSearchResponse
import com.practicum.playlistmaker.presentation.util.Util
import org.jsoup.Jsoup

class JsoupNetworkClient : NetworkClient {

    override fun doRequest(dto: Any): Response {
       return if (dto is SearchRequest) {

            try {
                val result = Jsoup.connect(dto.term).execute()
                val body = result.body()?.let {
                    TrackDescriptionSearchResponse(it)
                } ?: Response()

                body
            } catch (e: Exception) {
                Response().apply { resultCode = Util.HTTP_NOT_FOUND }
            }
        } else {
            Response().apply { resultCode = Util.HTTP_BAD_REQUEST }
        }
    }
}