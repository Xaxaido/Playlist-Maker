package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.main.ui.MainActivity
import com.practicum.playlistmaker.main.ui.view_model.MainActivityViewModel
import com.practicum.playlistmaker.medialibrary.ui.FavoriteTracksFragment
import com.practicum.playlistmaker.medialibrary.ui.PlaylistsFragment
import com.practicum.playlistmaker.medialibrary.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.medialibrary.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchFragment
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsFragment
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    DataModule::class,
    InteractorModule::class,
    RepositoryModule::class,
    ViewModelModule::class,
])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(appContext: Context): Builder
        fun build(): AppComponent
    }

    fun inject(app: App)
    fun inject(activity: MainActivity)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: FavoriteTracksFragment)
    fun inject(fragment: PlaylistsFragment)
    fun inject(fragment: SearchFragment)
    fun inject(fragment: PlayerFragment)

    fun inject(viewModel: MainActivityViewModel)
    fun inject(viewModel: SettingsViewModel)
    fun inject(viewModel: FavoriteTracksViewModel)
    fun inject(viewModel: PlaylistsViewModel)
    fun inject(viewModel: SearchViewModel)
    fun inject(viewModel: PlayerViewModel)
}