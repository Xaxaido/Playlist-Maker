package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {

    @GET("/search")
    fun searchTracks(
        @Query("term") text: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("country") country: String = "us",
        @Query("lang") lang: String = "ru_ru",
    ): Call<TracksSearchResponse>
}