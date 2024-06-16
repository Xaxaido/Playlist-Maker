package com.practicum.playlistmaker.extension.util

import com.practicum.playlistmaker.extension.network.ITunesService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    val iTunes= Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ITunesService::class.java)
}