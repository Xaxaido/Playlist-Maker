package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.RetrofitSearchRequest

interface RetrofitNetworkClient {
    suspend fun doRequest(dto: RetrofitSearchRequest): Response
}