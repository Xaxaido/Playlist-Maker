package com.practicum.playlistmaker.data.network

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.SearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(
    context: Context,
) : NetworkClient {

    private val baseUrl = context.getString(R.string.itunes_base_url)
    private val iTunesService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ITunesService::class.java)

    override fun doRequest(dto: Any): Response {

        if (dto is SearchRequest) {
            val result = iTunesService.searchTracks(dto.term).execute()
            val body = result.body() ?: Response()

            return body.apply { resultCode = result.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}