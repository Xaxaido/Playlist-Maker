package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.data.source.SearchTrackDescriptionData
import com.practicum.playlistmaker.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.impl.PrefsStorageRepositoryImpl
import com.practicum.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.impl.TrackDescriptionRepositoryImpl
import com.practicum.playlistmaker.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.JsoupNetworkClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.SharingRepository
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.PrefsStorageRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.SharingInteractor
import com.practicum.playlistmaker.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.data.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.domain.impl.TrackDescriptionInteractorImpl
import com.practicum.playlistmaker.presentation.settings.SettingsPresenter

object Creator {

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.prefs_file_name), MODE_PRIVATE)

    private fun getTracksRepository(context: Context): TracksRepository = TracksRepositoryImpl(RetrofitNetworkClient(context))
    fun getTracksInteractor(context: Context): TracksInteractor = TracksInteractorImpl(getTracksRepository(context))

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

    private fun getSettingsInteractor(context: Context): SettingsInteractor =
        SettingsInteractorImpl(getSettingsRepository(context))

    private fun getSharingInteractor(context: Context): SharingInteractor =
        SharingInteractorImpl(getSharingRepository(context))

    fun getSettingsPresenter(context: Context) = SettingsPresenter(
        getSettingsInteractor(context),
        getSharingInteractor(context),
    )
    /* Settings */

    private fun getPrefsStorageRepository(context: Context): PrefsStorageRepository =
        PrefsStorageRepositoryImpl(context, getPrefs(context))

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(getPrefsStorageRepository(context))

    fun getSearchHistoryInteractor(context: Context): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(getSearchHistoryRepository(context))

    /* Player */
    private fun getPlayerRepository(context: Context): PlayerRepository = PlayerRepositoryImpl(context)

    fun getPlayerInteractor(context: Context): PlayerInteractor =
        PlayerInteractorImpl(getPlayerRepository(context))
    /* Player */
}