package com.practicum.playlistmaker.di

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import androidx.media3.session.SessionToken
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.data.network.InternetConnection
import com.practicum.playlistmaker.player.ui.PlaybackService
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.impl.JsoupNetworkClientImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.JsoupNetworkClient
import com.practicum.playlistmaker.search.data.impl.RetrofitNetworkClientImpl
import com.practicum.playlistmaker.search.data.source.SearchTrackDescriptionData
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        context: Context,
    ): SharedPreferences {
        return context.getSharedPreferences(context.getString(R.string.prefs_file_name), Context.MODE_PRIVATE)
    }

    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    fun provideInternetConnection(): InternetConnection {
        return InternetConnection()
    }

    @Provides
    fun provideSessionToken(
        context: Context,
    ): SessionToken {
        return SessionToken(context, ComponentName(context, PlaybackService::class.java))
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        context: Context,
    ): ITunesService {
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.itunes_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesService::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkClient(itunesService: ITunesService): RetrofitNetworkClient {
        return RetrofitNetworkClientImpl(itunesService)
    }

    @Provides
    @Singleton
    fun provideNetworkClientBase(): JsoupNetworkClient {
        return JsoupNetworkClientImpl()
    }

    @Provides
    @Singleton
    fun provideSearchTrackDescriptionData(
        context: Context,
    ): SearchTrackDescriptionData {
        return SearchTrackDescriptionData(context)
    }
}