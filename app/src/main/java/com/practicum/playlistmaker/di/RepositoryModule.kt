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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindInternetConnectionRepository(
        internetConnectionRepositoryImpl: InternetConnectionRepositoryImpl
    ): InternetConnectionRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        searchHistoryRepositoryImpl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindTracksRepository(
        tracksRepositoryImpl: TracksRepositoryImpl
    ): TracksRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindTrackDescriptionRepository(
        trackDescriptionRepositoryImpl: TrackDescriptionRepositoryImpl
    ): TrackDescriptionRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindSharingRepository(
        sharingRepositoryImpl: SharingRepositoryImpl
    ): SharingRepository
}