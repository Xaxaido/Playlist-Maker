package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response

interface NetworkClientBase {
    fun doRequest(dto: Any): Response
}