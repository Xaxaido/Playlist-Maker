package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.SearchRequest
import com.practicum.playlistmaker.data.dto.TrackDescriptionSearchResponse
import org.jsoup.Jsoup

class JsoupNetworkClient : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is SearchRequest) {
            
            val result = Jsoup.connect(dto.term).execute()
            val body = result.body()?.let {
                TrackDescriptionSearchResponse(it)
            } ?: Response()
            
            return body.apply { resultCode = result.statusCode() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}