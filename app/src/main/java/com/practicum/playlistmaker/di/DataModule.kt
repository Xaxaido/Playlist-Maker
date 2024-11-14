package com.practicum.playlistmaker.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.SessionToken
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.data.network.InternetConnection
import com.practicum.playlistmaker.medialibrary.data.AppDataBase
import com.practicum.playlistmaker.player.ui.PlaybackService
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.impl.JsoupNetworkClientImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.JsoupNetworkClient
import com.practicum.playlistmaker.search.data.impl.RetrofitNetworkClientImpl
import com.practicum.playlistmaker.search.data.source.SearchTrackDescriptionData
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, androidContext().getString(R.string.database_file_name))
            .build()
    }

    single<ITunesService> {
        Retrofit.Builder()
            .baseUrl(androidContext().getString(R.string.itunes_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesService::class.java)
    }

    single<RetrofitNetworkClient> {
        RetrofitNetworkClientImpl(get())
    }

    single {
        androidContext().getSharedPreferences(androidContext()
            .getString(R.string.prefs_file_name), Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<JsoupNetworkClient> {
        JsoupNetworkClientImpl()
    }

    single<SearchTrackDescriptionData> {
        SearchTrackDescriptionData(get())
    }

    factory { SessionToken(
        androidContext(),
        ComponentName(androidContext(), PlaybackService::class.java)
    )}

    factory { InternetConnection() }
}