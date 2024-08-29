package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.data.PlaybackServiceTokenProvider
import com.practicum.playlistmaker.player.data.impl.PlaybackServiceTokenProviderImpl
import com.practicum.playlistmaker.search.data.source.SearchTrackDescriptionData
import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackDescriptionRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.network.JsoupNetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.sharing.data.api.SharingRepository
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.search.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.search.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TrackDescriptionInteractorImpl
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

object Creator {

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.prefs_file_name), MODE_PRIVATE)

    private fun getTracksRepository(context: Context): TracksRepository = TracksRepositoryImpl(
        RetrofitNetworkClient(context)
    )
    fun getTracksInteractor(context: Context): TracksInteractor = TracksInteractorImpl(
        getTracksRepository(context)
    )

    /* SearchTrackDescription */
    private fun getSearchTrackDescriptionData(context: Context): SearchTrackDescriptionData = SearchTrackDescriptionData(context)

    private fun getTrackDescriptionRepository(context: Context): TrackDescriptionRepository =
        TrackDescriptionRepositoryImpl(JsoupNetworkClient(), getSearchTrackDescriptionData(context))

    fun getTrackDescriptionInteractor(context: Context): TrackDescriptionInteractor =
        TrackDescriptionInteractorImpl(getTrackDescriptionRepository(context))
    /* SearchTrackDescription */

    /* Settings */
    private fun getSettingsRepository(context: Context): SettingsRepository =
        SettingsRepositoryImpl(context, getPrefs(context))

    private fun getSharingRepository(context: Context): SharingRepository = SharingRepositoryImpl(context)

    fun getSettingsInteractor(context: Context): SettingsInteractor =
        SettingsInteractorImpl(getSettingsRepository(context))

    fun getSharingInteractor(context: Context): SharingInteractor =
        SharingInteractorImpl(getSharingRepository(context))
    /* Settings */

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(context, getPrefs(context))

    fun getSearchHistoryInteractor(context: Context): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(getSearchHistoryRepository(context))

    /* Player */
    private fun getPlaybackServiceTokenProvider(): PlaybackServiceTokenProvider =
        PlaybackServiceTokenProviderImpl()

    private fun getPlayerRepository(context: Context): PlayerRepository =
        PlayerRepositoryImpl(context, getPlaybackServiceTokenProvider())

    fun getPlayerInteractor(context: Context): PlayerInteractor =
        PlayerInteractorImpl(getPlayerRepository(context))
    /* Player */
}