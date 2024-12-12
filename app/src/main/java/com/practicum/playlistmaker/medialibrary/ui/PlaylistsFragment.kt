package com.practicum.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.MediaLibraryState
import com.practicum.playlistmaker.common.resources.VisibilityState.Loading
import com.practicum.playlistmaker.common.resources.VisibilityState.NoData
import com.practicum.playlistmaker.common.resources.VisibilityState.Results
import com.practicum.playlistmaker.common.resources.VisibilityState.ViewsList
import com.practicum.playlistmaker.common.resources.VisibilityState.VisibilityItem
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.common.widgets.recycler.ItemAnimator
import com.practicum.playlistmaker.common.widgets.recycler.PaddingItemDecoration
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistListItem
import com.practicum.playlistmaker.medialibrary.ui.recycler.PlaylistAdapter
import com.practicum.playlistmaker.medialibrary.ui.recycler.PlaylistItemDecoration
import com.practicum.playlistmaker.medialibrary.ui.view_model.PlaylistsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: BaseFragment<FragmentPlaylistsBinding>() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var adapter: PlaylistAdapter
    private lateinit var visibility: ViewsList
    private var isClickEnabled = true

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setListeners()
    }

    private fun setupUI() {
        visibility = ViewsList(
            listOf(
                VisibilityItem(binding.progressBar, listOf(Loading)),
                VisibilityItem(binding.noPlaylists, listOf(NoData)),
                VisibilityItem(binding.newPlaylistContainer, listOf(NoData, Results)),
                VisibilityItem(binding.recycler, listOf(Results)),
            )
        )

        adapter = PlaylistAdapter { playlist ->
            findNavController().navigate(
                R.id.action_open_playlist,
                PlaylistFragment.createArgs(playlist.id),
            )
        }

        binding.recycler.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.recycler.adapter = adapter
        binding.recycler.itemAnimator = ItemAnimator()
        binding.recycler.addItemDecoration(
            PaddingItemDecoration(
                intArrayOf(
                    0,
                    resources.getDimensionPixelSize(R.dimen.toolbar_height),
                )
            )
        )
        binding.recycler.addItemDecoration(PlaylistItemDecoration(
            2,
            resources.getDimensionPixelSize(R.dimen.padding_small_6x),
            resources.getDimensionPixelSize(R.dimen.padding_small_8x),
        )
        )

        binding.blurImageViewBottomMenu.setContentView(binding.recycler)
    }

    private fun setListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { state ->
                    renderState(state)
                }
            }
        }

        binding.createPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_create_playlist)
        }
    }

    private fun showPlaylists(list: List<Playlist>) {
        val items = adapter.convertToPlaylistListItem(PlaylistListItem.PlaylistItem::class.java, list)
        adapter.submitTracksList(items)  {
            visibility.show(Results)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun renderState(state: MediaLibraryState) {
        when (state) {
            is MediaLibraryState.Loading -> visibility.show(Loading)
            is MediaLibraryState.Empty -> visibility.show(NoData)
            is MediaLibraryState.Content<*> -> showPlaylists(state.list as List<Playlist>)
        }
    }
}