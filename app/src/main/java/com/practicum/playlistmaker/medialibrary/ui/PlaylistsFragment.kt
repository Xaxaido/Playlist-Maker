package com.practicum.playlistmaker.medialibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.medialibrary.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: BaseFragment<FragmentPlaylistsBinding>() {

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    companion object {

        fun newInstance() = PlaylistsFragment()
    }
}