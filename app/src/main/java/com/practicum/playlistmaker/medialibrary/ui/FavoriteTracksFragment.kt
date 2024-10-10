package com.practicum.playlistmaker.medialibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.di.api.DaggerViewModelFactory
import com.practicum.playlistmaker.medialibrary.view_model.FavoriteTracksViewModel
import javax.inject.Inject

class FavoriteTracksFragment: BaseFragment<FragmentFavoriteTracksBinding>() {

    @Inject
    lateinit var viewModelFactory: DaggerViewModelFactory
    private lateinit var viewModel: FavoriteTracksViewModel

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoriteTracksBinding {
        (requireActivity().applicationContext as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[FavoriteTracksViewModel::class.java]
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    companion object {

        fun newInstance() = FavoriteTracksFragment()
    }
}