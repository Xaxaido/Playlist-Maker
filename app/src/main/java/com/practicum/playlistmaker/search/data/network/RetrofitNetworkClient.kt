package com.practicum.playlistmaker.search.data.network

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient(
    context: Context,
) : NetworkClient {

    private val baseUrl = context.getString(R.string.itunes_base_url)
    private var call: Call<TracksSearchResponse>? = null
    private val iTunesService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ITunesService::class.java)

    override fun doRequest(dto: SearchRequest): Response {
        return try {
            call = iTunesService.searchTracks(dto.term)
            val result = call?.execute()

            result?.body()?.apply {
                resultCode = result.code()
            } ?: Response()
        } catch (e: IOException) {
            Response().apply {
                resultCode = when (e.message) {
                    "Canceled" -> Util.REQUEST_CANCELLED
                    else -> Util.REQUEST_TIMEOUT
                }
            }
        }
    }

    override fun cancelRequest() { call?.cancel() }
}