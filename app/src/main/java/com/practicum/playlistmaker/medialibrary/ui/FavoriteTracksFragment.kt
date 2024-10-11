package com.practicum.playlistmaker.medialibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.medialibrary.view_model.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment: BaseFragment<FragmentFavoriteTracksBinding>() {

    private val viewModel by viewModel<FavoriteTracksViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    companion object {

        fun newInstance() = FavoriteTracksFragment()
    }
}