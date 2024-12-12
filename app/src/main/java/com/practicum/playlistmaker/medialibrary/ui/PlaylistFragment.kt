package com.practicum.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlaylistState
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Extensions.millisToMinutes
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.common.widgets.recycler.ItemAnimator
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.recycler.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment: BaseFragment<FragmentPlaylistBinding>() {

    companion object {
        private const val ARGS_PLAYLIST = "ARGS_PLAYLIST"

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST to playlistId)
    }

    private val viewModel by viewModel<PlaylistViewModel> {
        parametersOf(requireArguments().getInt(ARGS_PLAYLIST))
    }
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var track: Track
    private var isClickEnabled = true

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setListeners()
    }

    private fun setupUI() {
        (activity as? BackButtonState)?.setIconColor(false)

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setMessage(resources.getString(R.string.playlist_dialog_message))
            .setNeutralButton(resources.getString(R.string.dialog_message_no)) { _, _ ->
            }.setPositiveButton(resources.getString(R.string.dialog_message_yes)) { _, _ ->
                viewModel.removeTrack(track.id)
            }

        trackAdapter = TrackAdapter(
            { track ->
                if (!isClickEnabled) return@TrackAdapter

                isClickEnabled = false
                sendToPlayer(Util.trackToJson(track))
                Debounce<Any>(Util.BUTTON_ENABLED_DELAY, lifecycleScope) { isClickEnabled = true }.start()
            },
            { track ->
                this.track = track
                confirmDialog.show()
                true
            },
            showFavorites = false
            )
        binding.recycler.adapter = trackAdapter
        binding.recycler.itemAnimator = ItemAnimator()
        binding.blurImageViewBottomMenu.setContentView(binding.recycler)
    }

    private fun setListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistFlow.collect {
                    renderState(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracksLIstFlow.collect {
                    showTracksList(it)
                }
            }
        }
    }

    private fun sendToPlayer(json: String) {
        findNavController().navigate(
            R.id.action_send_to_player,
            PlayerFragment.createArgs(json),
        )
    }

    private fun fillPlaylistData(playlist: Playlist, duration: Long) {
        with (binding) {
            Glide.with(this@PlaylistFragment)
                .load(playlist.cover)
                .placeholder(R.drawable.playlist_cover_placeholder)
                .centerCrop()
                .into(cover)

            playlistTitle.text = playlist.name
            description.text = playlist.description

            val durationInMinutes = duration.millisToMinutes().toInt()
            summary.setText(
                "$durationInMinutes ${Util.formatValue(durationInMinutes, getString(R.string.playlist_minute))}",
                "${playlist.tracksCount} ${Util.formatValue(playlist.tracksCount, getString(R.string.playlist_track))}"
            )
        }
    }

    private fun showTracksList(list: List<Track>) {
        trackAdapter.submitTracksList(false, list, false)
    }

    private fun renderState(state: PlaylistState) {
        when (state) {
            is PlaylistState.PlaylistInfo -> fillPlaylistData(state.playlist, state.duration)
        }
    }
}