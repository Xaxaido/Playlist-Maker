package com.practicum.playlistmaker.extension.network

import android.content.Context
import com.practicum.playlistmaker.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    private var _iTunes: ITunesService? = null
    val iTunes: ITunesService?
        get() = _iTunes

    fun initialize(context: Context) {
        val baseUrl = context.getString(R.string.itunes_base_url)

        _iTunes = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesService::class.java)
    }
}