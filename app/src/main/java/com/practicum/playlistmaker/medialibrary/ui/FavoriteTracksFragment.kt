package com.practicum.playlistmaker.medialibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.practicum.playlistmaker.common.widgets.BindingFragment
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.medialibrary.view_model.FavoriteTracksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteTracksFragment: BindingFragment<FragmentFavoriteTracksBinding>() {

    private val viewModel: FavoriteTracksViewModel by activityViewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    companion object {

        fun newInstance() = FavoriteTracksFragment().apply {

        }
    }
}