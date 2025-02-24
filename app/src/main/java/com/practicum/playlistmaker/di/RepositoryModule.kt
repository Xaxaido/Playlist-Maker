package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.data.impl.InternetConnectionRepositoryImpl
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository
import com.practicum.playlistmaker.medialibrary.data.db.impl.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.medialibrary.data.db.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.medialibrary.data.db.impl.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.player.data.impl.TrackDescriptionRepositoryImpl
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

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    single<TrackDescriptionRepository> {
        TrackDescriptionRepositoryImpl(get(), get())
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