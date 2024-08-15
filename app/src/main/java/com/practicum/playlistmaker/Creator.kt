package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.data.PrefsStorageRepositoryImpl
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.impl.TracksMediatorImpl
import com.practicum.playlistmaker.presentation.settings.ExternalNavigatorImpl
import com.practicum.playlistmaker.presentation.settings.SettingsPresenter

object Creator {

    private fun getTracksRepository(context: Context) = TracksRepositoryImpl(RetrofitNetworkClient(context))
    fun provideTracksMediator(context: Context) = TracksMediatorImpl(getTracksRepository(context))

    fun getPrefs(context: Context) = context.getSharedPreferences(context.getString(R.string.prefs_file_name), MODE_PRIVATE)

    private fun getExternalNavigatorImpl(context: Context) = ExternalNavigatorImpl(context)
    private fun getSettingsRepositoryImpl(context: Context) = SettingsRepositoryImpl(context, getPrefs(context))
    fun provideSettingsPresenter(context: Context) = SettingsPresenter(
        getExternalNavigatorImpl(context),
        getSettingsRepositoryImpl(context),
    )

    private fun getPrefsStorageRepositoryImpl(context: Context) = PrefsStorageRepositoryImpl(context, getPrefs(context))
    fun getSearchHistoryRepositoryImpl(context: Context) = SearchHistoryRepositoryImpl(getPrefsStorageRepositoryImpl(context))
}