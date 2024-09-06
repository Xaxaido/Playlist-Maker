package com.practicum.playlistmaker.search.data.network

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient(
    context: Context,
) : NetworkClient {

    private val baseUrl = context.getString(R.string.itunes_base_url)
    private val iTunesService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ITunesService::class.java)
    private var call: Call<TracksSearchResponse>? = null

    override fun doRequest(dto: Any): Response {
        return if (dto is SearchRequest) {
            try {
                call = iTunesService.searchTracks(dto.term)
                val result = call?.execute()
                val body = result?.body() ?: Response()

                body.apply { resultCode = result?.code() ?: Util.HTTP_BAD_REQUEST }
            } catch (e: IOException) {
                return Response().apply {
                    resultCode = if (e.message == "Canceled") Util.REQUEST_CANCELLED
                    else Util.REQUEST_TIMEOUT
                }
            }
        } else {
            Response().apply { resultCode = Util.HTTP_BAD_REQUEST }
        }
    }

    override fun cancelRequest() { call?.cancel() }
}