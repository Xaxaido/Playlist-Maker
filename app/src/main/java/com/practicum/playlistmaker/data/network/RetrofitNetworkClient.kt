package com.practicum.playlistmaker.data.network

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.SearchRequest
import com.practicum.playlistmaker.data.util.Extensions
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

        return if (dto is SearchRequest) {
            val result = iTunesService.searchTracks(dto.term).execute()
            val body = result.body() ?: Response()

            body.apply { resultCode = result.code() }
        } else {
            Response().apply { resultCode = Extensions.HTTP_BAD_REQUEST }
        }
    }
}