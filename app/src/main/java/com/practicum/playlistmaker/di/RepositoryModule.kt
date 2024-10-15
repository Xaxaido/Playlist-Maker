package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.data.impl.InternetConnectionRepositoryImpl
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository
import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.data.impl.TrackDescriptionRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.sharing.data.api.SharingRepository
import com.practicum.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    single<TrackDescriptionRepository> {
        TrackDescriptionRepositoryImpl(get(), get())
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(), get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

    single<InternetConnectionRepository> {
        InternetConnectionRepositoryImpl(get(), get())
    }
}