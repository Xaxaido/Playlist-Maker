package com.practicum.playlistmaker.extension.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {

    @GET("/search")
    fun search(
        @Query("term") text: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("country") country: String = "us",
        @Query("lang") lang: String = "ru_ru",
    ): Call<TrackResponse>
}