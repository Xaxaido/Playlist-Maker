package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.main.domain.impl.InternetConnectionInteractorImpl
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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InteractorModule {

    @Binds
    @Singleton
    abstract fun bindInternetConnectionInteractor(
        internetConnectionInteractorImpl: InternetConnectionInteractorImpl
    ): InternetConnectionInteractor

    @Binds
    @Singleton
    abstract fun bindSearchHistoryInteractor(
        searchHistoryInteractorImpl: SearchHistoryInteractorImpl
    ): SearchHistoryInteractor

    @Binds
    @Singleton
    abstract fun bindTracksInteractor(
        tracksInteractorImpl: TracksInteractorImpl
    ): TracksInteractor

    @Binds
    @Singleton
    abstract fun bindPlayerInteractor(
        playerInteractorImpl: PlayerInteractorImpl
    ): PlayerInteractor

    @Binds
    @Singleton
    abstract fun bindTrackDescriptionInteractor(
        trackDescriptionInteractorImpl: TrackDescriptionInteractorImpl
    ): TrackDescriptionInteractor

    @Binds
    @Singleton
    abstract fun bindSettingsInteractor(
        settingsInteractorImpl: SettingsInteractorImpl
    ): SettingsInteractor

    @Binds
    @Singleton
    abstract fun bindSharingInteractor(
        sharingInteractorImpl: SharingInteractorImpl
    ): SharingInteractor
}