package com.practicum.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.data.network.InternetConnection
import com.practicum.playlistmaker.player.data.PlaybackServiceTokenProvider
import com.practicum.playlistmaker.player.data.impl.PlaybackServiceTokenProviderImpl
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.network.JsoupNetworkClient
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.NetworkClientBase
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.source.SearchTrackDescriptionData
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesService> {
        Retrofit.Builder()
            .baseUrl(androidContext().getString(R.string.itunes_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        androidContext()
            .getSharedPreferences(androidContext()
            .getString(R.string.prefs_file_name), Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClientBase> {
        JsoupNetworkClient()
    }

    single<SearchTrackDescriptionData> {
        SearchTrackDescriptionData(get())
    }

    single<PlaybackServiceTokenProvider> {
        PlaybackServiceTokenProviderImpl()
    }

    factory { InternetConnection() }
}