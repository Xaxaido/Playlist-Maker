package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.data.JsoupRepositoryImpl
import com.practicum.playlistmaker.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.PrefsStorageRepositoryImpl
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.TrackDescriptionRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.JsoupNetworkClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.ExternalNavigator
import com.practicum.playlistmaker.domain.api.JsoupRepository
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.PrefsStorageRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.domain.api.TrackDescriptionRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.presentation.settings.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TrackDescriptionInteractorImpl
import com.practicum.playlistmaker.presentation.settings.SettingsPresenter

object Creator {

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.prefs_file_name), MODE_PRIVATE)

    private fun getTracksRepository(context: Context): TracksRepository = TracksRepositoryImpl(RetrofitNetworkClient(context))
    fun getTracksMediator(context: Context): TracksInteractor = TracksInteractorImpl(getTracksRepository(context))


    private fun getJsoupRepository(context: Context): JsoupRepository = JsoupRepositoryImpl(context)

    private fun getTrackDescriptionRepository(context: Context): TrackDescriptionRepository =
        TrackDescriptionRepositoryImpl(JsoupNetworkClient(), getJsoupRepository(context))

    fun getTrackDescriptionMediator(context: Context): TrackDescriptionInteractor =
        TrackDescriptionInteractorImpl(getTrackDescriptionRepository(context))

    private fun getExternalNavigator(context: Context): ExternalNavigator = ExternalNavigatorImpl(context)

    private fun getSettingsRepository(context: Context): SettingsRepository =
        SettingsRepositoryImpl(context, getPrefs(context))

    fun getSettingsPresenter(context: Context) = SettingsPresenter(
        getExternalNavigator(context),
        getSettingsRepository(context),
    )

    private fun getPrefsStorageRepository(context: Context): PrefsStorageRepository =
        PrefsStorageRepositoryImpl(context, getPrefs(context))

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(getPrefsStorageRepository(context))

    fun getSearchHistoryMediator(context: Context): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(getSearchHistoryRepository(context))

    fun getPlayerRepository(context: Context): PlayerRepository = PlayerRepositoryImpl(context)
}