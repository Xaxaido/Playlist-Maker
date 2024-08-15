package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.data.PrefsStorageRepositoryImpl
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.impl.TracksMediatorImpl
import com.practicum.playlistmaker.domain.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.impl.SearchHistoryMediatorImpl
import com.practicum.playlistmaker.presentation.settings.SettingsPresenter

object Creator {

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.prefs_file_name), MODE_PRIVATE)

    private fun getTracksRepository(context: Context) = TracksRepositoryImpl(RetrofitNetworkClient(context))
    fun getTracksMediator(context: Context) = TracksMediatorImpl(getTracksRepository(context))

    private fun getExternalNavigator(context: Context) = ExternalNavigatorImpl(context)
    private fun getSettingsRepository(context: Context) = SettingsRepositoryImpl(context, getPrefs(context))
    fun getSettingsPresenter(context: Context) = SettingsPresenter(
        getExternalNavigator(context),
        getSettingsRepository(context),
    )

    private fun getPrefsStorageRepository(context: Context) = PrefsStorageRepositoryImpl(context, getPrefs(context))
    private fun getSearchHistoryRepository(context: Context) = SearchHistoryRepositoryImpl(getPrefsStorageRepository(context))
    fun getSearchHistoryMediator(context: Context) = SearchHistoryMediatorImpl(getSearchHistoryRepository(context))
}