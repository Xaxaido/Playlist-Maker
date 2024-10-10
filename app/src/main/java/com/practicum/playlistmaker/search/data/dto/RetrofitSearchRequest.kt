package com.practicum.playlistmaker.search.data.dto

data class RetrofitSearchRequest(
    val term: String,
    val page: Int = 0,
)