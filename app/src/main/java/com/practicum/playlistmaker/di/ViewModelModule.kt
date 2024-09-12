package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.ui.view_model.MainActivityModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainActivityModel(get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}