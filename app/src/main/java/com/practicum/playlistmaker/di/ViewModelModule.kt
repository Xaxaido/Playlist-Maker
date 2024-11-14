package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.ui.view_model.MainActivityViewModel
import com.practicum.playlistmaker.medialibrary.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.medialibrary.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainActivityViewModel(get())
    }

    viewModel {
        SearchViewModel(get(), get(), get(), get())
    }

    viewModel { (json: String) ->
        PlayerViewModel(get(), get(), get(), json)
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel()
    }
}