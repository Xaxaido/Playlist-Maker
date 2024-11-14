package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.JsoupSearchRequest

interface JsoupNetworkClient {
    suspend fun doRequest(dto: JsoupSearchRequest): Response
}