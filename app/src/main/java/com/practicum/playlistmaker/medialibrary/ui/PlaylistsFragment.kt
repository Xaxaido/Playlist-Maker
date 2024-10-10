package com.practicum.playlistmaker.medialibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.di.api.DaggerViewModelFactory
import com.practicum.playlistmaker.medialibrary.view_model.PlaylistsViewModel
import javax.inject.Inject

class PlaylistsFragment: BaseFragment<FragmentPlaylistsBinding>() {

    @Inject
    lateinit var viewModelFactory: DaggerViewModelFactory
    private lateinit var viewModel: PlaylistsViewModel

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistsBinding {
        (requireActivity().applicationContext as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[PlaylistsViewModel::class.java]
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    companion object {

        fun newInstance() = PlaylistsFragment()
    }
}