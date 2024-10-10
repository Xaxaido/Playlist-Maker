package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.RetrofitSearchRequest

interface RetrofitNetworkClient {
    fun doRequest(dto: RetrofitSearchRequest): Response
    fun cancelRequest()
}