package com.practicum.playlistmaker.di

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.di.api.ViewModelKey
import com.practicum.playlistmaker.main.ui.view_model.MainActivityViewModel
import com.practicum.playlistmaker.medialibrary.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.medialibrary.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(myViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(myViewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteTracksViewModel::class)
    abstract fun bindFavoriteTracksViewModel(myViewModel: FavoriteTracksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistsViewModel::class)
    abstract fun bindPlaylistsViewModel(myViewModel: PlaylistsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(myViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun bindPlayerViewModel(myViewModel: PlayerViewModel): ViewModel
}