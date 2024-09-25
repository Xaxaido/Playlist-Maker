package com.practicum.playlistmaker.di

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.main.data.impl.InternetConnectionRepositoryImpl
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository
import com.practicum.playlistmaker.main.domain.impl.InternetConnectionInteractorImpl
import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.data.impl.TrackDescriptionRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.player.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.impl.TrackDescriptionInteractorImpl
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.api.SharingRepository
import com.practicum.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideApplicationContext(application: Application): Context

    @Binds
    @Singleton
    abstract fun bindInternetConnectionRepository(
        internetConnectionRepositoryImpl: InternetConnectionRepositoryImpl
    ): InternetConnectionRepository

    @Binds
    @Singleton
    abstract fun bindInternetConnectionInteractor(
        internetConnectionInteractorImpl: InternetConnectionInteractorImpl
    ): InternetConnectionInteractor

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        searchHistoryRepositoryImpl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryInteractor(
        searchHistoryInteractorImpl: SearchHistoryInteractorImpl
    ): SearchHistoryInteractor

    @Binds
    @Singleton
    abstract fun bindTracksRepository(
        tracksRepositoryImpl: TracksRepositoryImpl
    ): TracksRepository

    @Binds
    @Singleton
    abstract fun bindTracksInteractor(
        tracksInteractorImpl: TracksInteractorImpl
    ): TracksInteractor

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindPlayerInteractor(
        playerInteractorImpl: PlayerInteractorImpl
    ): PlayerInteractor

    @Binds
    @Singleton
    abstract fun bindTrackDescriptionRepository(
        trackDescriptionRepositoryImpl: TrackDescriptionRepositoryImpl
    ): TrackDescriptionRepository

    @Binds
    @Singleton
    abstract fun bindTrackDescriptionInteractor(
        trackDescriptionInteractorImpl: TrackDescriptionInteractorImpl
    ): TrackDescriptionInteractor

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository


    @Binds
    @Singleton
    abstract fun bindSettingsInteractor(
        settingsInteractorImpl: SettingsInteractorImpl
    ): SettingsInteractor

    @Binds
    @Singleton
    abstract fun bindSharingRepository(
        sharingRepositoryImpl: SharingRepositoryImpl
    ): SharingRepository


    @Binds
    @Singleton
    abstract fun bindSharingInteractor(
        sharingInteractorImpl: SharingInteractorImpl
    ): SharingInteractor
}