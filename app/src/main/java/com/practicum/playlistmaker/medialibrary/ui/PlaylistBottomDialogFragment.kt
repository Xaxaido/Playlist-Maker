package com.practicum.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlaylistState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.BaseBottomDialogFragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistBottomDialogBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.ui.view_model.PlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistBottomDialogFragment : BaseBottomDialogFragment<FragmentPlaylistBottomDialogBinding>() {

    companion object {
        const val TAG = "PlaylistBottomDialogFragment"

        fun newInstance(): PlaylistBottomDialogFragment {
            return PlaylistBottomDialogFragment()
        }
    }

    private val viewModel by viewModel<PlaylistViewModel>(ownerProducer = { requireParentFragment() } )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBottomDialogBinding {
        return FragmentPlaylistBottomDialogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun fillPlaylistData(playlist: Playlist) {
        val count = Util.formatValue(playlist.tracksCount, getString(R.string.playlist_track))

        with (binding.playlist) {
            Glide.with(this@PlaylistBottomDialogFragment)
                .load(playlist.cover)
                .placeholder(R.drawable.playlist_cover_placeholder)
                .centerCrop()
                .into(cover)

            playlistTitle.text = playlist.name
            tracksCount.text = count
        }
    }

    private fun setListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistFlow.collect { state ->
                    if (state is PlaylistState.PlaylistInfo) {
                        fillPlaylistData(state.playlist)
                    }
                }
            }
        }

        binding.sharePlaylist.setOnClickListener {
            dismiss()
            viewModel.sharePlaylist()
        }

        binding.editPlaylist.setOnClickListener {
            dismiss()

            findNavController().navigate(
                R.id.action_create_playlist,
                CreatePlaylistFragment.createArgs(viewModel.playlist),
            )
        }

        binding.removePlaylist.setOnClickListener {
            dismiss()

            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.remove_playlist))
                .setMessage(resources.getString(R.string.remove_playlist_message))
                .setNegativeButton(resources.getString(R.string.dialog_message_no)) { _, _ ->
                }.setPositiveButton(resources.getString(R.string.dialog_message_yes)) { _, _ ->
                    viewModel.removePlaylist()
                }.show()
        }
    }
}