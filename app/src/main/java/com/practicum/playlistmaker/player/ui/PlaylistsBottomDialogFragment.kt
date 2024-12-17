package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlaylistsBottomDialogFragmentState
import com.practicum.playlistmaker.common.utils.MySnackBar
import com.practicum.playlistmaker.common.widgets.BaseBottomDialogFragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBottomDialogBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistListItem
import com.practicum.playlistmaker.medialibrary.ui.CreatePlaylistFragment
import com.practicum.playlistmaker.medialibrary.ui.recycler.PlaylistAdapter
import com.practicum.playlistmaker.player.ui.view_model.PlaylistsBottomSheetDialogViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsBottomDialogFragment(
    private val track: Track,
) : BaseBottomDialogFragment<FragmentPlaylistsBottomDialogBinding>() {

    companion object {
        const val TAG = "PlaylistsBottomDialogFragment"

        fun newInstance(track: Track): PlaylistsBottomDialogFragment {
            return PlaylistsBottomDialogFragment(track)
        }
    }

    private val viewModel by viewModel<PlaylistsBottomSheetDialogViewModel> {
        parametersOf(track)
    }

    private lateinit var playlistAdapter: PlaylistAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBottomDialogBinding {
        return FragmentPlaylistsBottomDialogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setListeners()
    }

    private fun setupUI() {
        playlistAdapter = PlaylistAdapter { playlist ->
            viewModel.addToPlaylist(playlist)
        }

        binding.playlistsRecycler.adapter = playlistAdapter
    }

    private fun setListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistsFlow.collect { playlists ->
                    showPlaylists(playlists)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToPlaylistFlow.collect { state ->
                    if (state is PlaylistsBottomDialogFragmentState.AddToPlaylist) {
                        showAddToPlaylistStatus(state.playlistTitle, state.isAdded)
                    }
                }
            }
        }

        binding.createPlaylistBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_create_playlist,
                CreatePlaylistFragment.createArgs(null),
            )
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        val items = playlistAdapter.convertToPlaylistListItem(PlaylistListItem.PlaylistBottomSheetItem::class.java, playlists)
        playlistAdapter.submitTracksList(items)
    }

    private fun showAddToPlaylistStatus(playlistTitle: String, isAdded: Boolean) {
        val view: View
        val message = if (isAdded) {
            dismiss()
            view = requireActivity().findViewById(R.id.content_layout)
            resources.getText(R.string.add_to_playlist_success)
        } else {
            view = requireView()
            resources.getText(R.string.add_to_playlist_error)
        }

        MySnackBar(view, "$message $playlistTitle").show()
    }
}