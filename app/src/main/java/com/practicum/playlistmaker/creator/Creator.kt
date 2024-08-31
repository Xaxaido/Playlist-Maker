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

object Creator {

    private lateinit var _appContext: Context
    val appContext get() = _appContext

    fun init(appContext: Context) {
        _appContext = appContext
    }

    private fun getPrefs(): SharedPreferences =
        _appContext.getSharedPreferences(_appContext.getString(R.string.prefs_file_name), MODE_PRIVATE)

    /* SearchTrack */
    private fun getTracksRepository(): TracksRepository = TracksRepositoryImpl(
        RetrofitNetworkClient(_appContext)
    )
    fun getTracksInteractor(): TracksInteractor = TracksInteractorImpl(
        getTracksRepository()
    )

    /* SearchTrackDescription */
    private fun getSearchTrackDescriptionData(): SearchTrackDescriptionData = SearchTrackDescriptionData(_appContext)

    private fun getTrackDescriptionRepository(): TrackDescriptionRepository =
        TrackDescriptionRepositoryImpl(JsoupNetworkClient(), getSearchTrackDescriptionData())

    fun getTrackDescriptionInteractor(): TrackDescriptionInteractor =
        TrackDescriptionInteractorImpl(getTrackDescriptionRepository())

    /* Settings */
    private fun getSettingsRepository(): SettingsRepository =
        SettingsRepositoryImpl(_appContext, getPrefs())

    private fun getSharingRepository(): SharingRepository = SharingRepositoryImpl(_appContext)

    fun getSettingsInteractor(): SettingsInteractor =
        SettingsInteractorImpl(getSettingsRepository())

    fun getSharingInteractor(): SharingInteractor =
        SharingInteractorImpl(getSharingRepository())

    /* SearchHistory */
    private fun getSearchHistoryRepository(): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(_appContext, getPrefs())

    fun getSearchHistoryInteractor(): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(getSearchHistoryRepository())

    /* Player */
    private fun getPlaybackServiceTokenProvider(): PlaybackServiceTokenProvider =
        PlaybackServiceTokenProviderImpl()

    private fun getPlayerRepository(): PlayerRepository =
        PlayerRepositoryImpl(_appContext, getPlaybackServiceTokenProvider())

    fun getPlayerInteractor(): PlayerInteractor =
        PlayerInteractorImpl(getPlayerRepository())
}