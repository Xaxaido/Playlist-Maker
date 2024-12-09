package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.main.domain.impl.InternetConnectionInteractorImpl
import com.practicum.playlistmaker.medialibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.impl.FavoriteTracksInteractorImpl
import com.practicum.playlistmaker.medialibrary.domain.impl.PlaylistsInteractorImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.impl.TrackDescriptionInteractorImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<TrackDescriptionInteractor> {
        TrackDescriptionInteractorImpl(get())
    }

    single<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    single<InternetConnectionInteractor> {
        InternetConnectionInteractorImpl(get())
    }
}