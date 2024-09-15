package com.practicum.playlistmaker.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.SessionToken
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.data.network.InternetConnection
import com.practicum.playlistmaker.player.ui.PlaybackService
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

    single {
        androidContext().let {
            it.getSharedPreferences(it.getString(R.string.prefs_file_name), Context.MODE_PRIVATE)
        }
    }

    factory { Gson() }

    factory { InternetConnection() }

    factory {
        androidContext().let {
            SessionToken(it, ComponentName(it, PlaybackService::class.java))
        }
    }

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

    single<NetworkClientBase> {
        JsoupNetworkClient()
    }

    single<SearchTrackDescriptionData> {
        SearchTrackDescriptionData(get())
    }
}