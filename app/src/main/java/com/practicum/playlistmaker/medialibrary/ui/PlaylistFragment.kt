package com.practicum.playlistmaker.medialibrary.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlaylistMenuState
import com.practicum.playlistmaker.common.resources.PlaylistState
import com.practicum.playlistmaker.common.resources.VisibilityState.NoData
import com.practicum.playlistmaker.common.resources.VisibilityState.Results
import com.practicum.playlistmaker.common.resources.VisibilityState.ViewsList
import com.practicum.playlistmaker.common.resources.VisibilityState.VisibilityItem
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Extensions.millisToMinutes
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.common.utils.MySnackBar
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.common.widgets.recycler.ItemAnimator
import com.practicum.playlistmaker.common.widgets.recycler.SwipeHelper
import com.practicum.playlistmaker.common.widgets.recycler.UnderlayButton
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
    private lateinit var visibility: ViewsList
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var swipeHelper: SwipeHelper
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

        visibility = ViewsList(
            listOf(
                VisibilityItem(binding.emptyMedialibrary, listOf(NoData)),
                VisibilityItem(binding.recycler, listOf(Results)),
            )
        )

        trackAdapter = TrackAdapter(
            { track ->
                if (!isClickEnabled) return@TrackAdapter

                isClickEnabled = false
                sendToPlayer(Util.trackToJson(track))
                Debounce<Any>(Util.BUTTON_ENABLED_DELAY, lifecycleScope) { isClickEnabled = true }.start()
            },
            { pos, track ->
                showDialog { removeTrack(pos, track.id) }
                true
            },
            showFavorites = false
            )
        binding.recycler.adapter = trackAdapter
        binding.recycler.itemAnimator = ItemAnimator()
        swipeHelper = initSwipeHelper()
    }

    private fun showDialog(action: () -> Unit) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.remove_track_title))
            .setMessage(getString(R.string.remove_track_message))
            .setNeutralButton(resources.getString(R.string.dialog_message_no)) { _, _ ->
            }.setPositiveButton(resources.getString(R.string.dialog_message_yes)) { _, _ ->
                action()
            }.show()
    }

    private fun initSwipeHelper() = object : SwipeHelper(binding.recycler) {

        override fun instantiateUnderlayButton(pos: Int): MutableList<UnderlayButton> = mutableListOf(btnDelete())
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
                viewModel.tracksListFlow.collect { tracks ->
                    if (tracks.isEmpty()) {
                        visibility.show(NoData)
                    } else {
                        showTracksList(tracks)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistMenuFlow.collect {
                    renderMenu(it)
                }
            }
        }


        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.isVisible = newState != BottomSheetBehavior.STATE_COLLAPSED
                        && newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                try {
                    binding.overlay.alpha = slideOffset
                } catch (_: Exception) {

                }
            }
        })

        binding.menu.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.share.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.sharePlaylist.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.editPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_create_playlist,
                CreatePlaylistFragment.createArgs(viewModel.playlist),
            )
        }

        binding.removePlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.remove_playlist))
                .setMessage(resources.getString(R.string.remove_playlist_message))
                .setNeutralButton(resources.getString(R.string.dialog_message_no)) { _, _ ->
                }.setPositiveButton(resources.getString(R.string.dialog_message_yes)) { _, _ ->
                    viewModel.removePlaylist()
                }.show()
        }
    }

    private fun sendToPlayer(json: String) {
        findNavController().navigate(
            R.id.action_send_to_player,
            PlayerFragment.createArgs(json),
        )
    }

    private fun fillPlaylistData(playlist: Playlist, duration: Long) {
        val count = Util.formatValue(playlist.tracksCount, getString(R.string.playlist_track))

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
                Util.formatValue(durationInMinutes, getString(R.string.playlist_minute)),
                count
            )
        }

        with (binding.playlist) {
            Glide.with(this@PlaylistFragment)
                .load(playlist.cover)
                .placeholder(R.drawable.playlist_cover_placeholder)
                .centerCrop()
                .into(cover)

            playlistTitle.text = playlist.name
            tracksCount.text = count
        }
    }

    private fun showTracksList(list: List<Track>) {
        trackAdapter.submitTracksList(false, list, false) {
            visibility.show(Results)
        }
    }

    private fun sharePlaylist(playlist: Playlist, list: List<Track>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (list.isEmpty()) {
            MySnackBar(
                requireView(),
                getString(R.string.share_playlist_error)
            ).show()
            return
        }

        val message = buildString {
            appendLine(playlist.name)
            appendLine(playlist.description)
            appendLine(Util.formatValue(playlist.tracksCount, getString(R.string.playlist_track)))
            appendLine()
            list.forEachIndexed { index, track ->
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.duration.millisToSeconds()})")
            }
        }

        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(this, null))
        }
    }

    private fun renderState(state: PlaylistState) {
        when (state) {
            is PlaylistState.PlaylistInfo -> fillPlaylistData(state.playlist, state.duration)
        }
    }

    private fun renderMenu(state: PlaylistMenuState) {
        when (state) {
            is PlaylistMenuState.Share -> sharePlaylist(state.playlist, state.tracks)
            is PlaylistMenuState.Remove -> findNavController().navigateUp()
        }
    }

    private fun removeTrack(pos: Int, id: Long) {
        swipeHelper.disableClick()
        trackAdapter.notifyItemChanged(pos)
        Debounce<Any>(Util.ANIMATION_SHORT, viewLifecycleOwner.lifecycleScope) {
            swipeHelper.startParticleAnimation(binding.particleView, pos) {
                viewModel.removeTrack(id)
                swipeHelper.enableClick()
            }
        }.start()
    }

    private val btnDelete: () -> UnderlayButton = {
        UnderlayButton(
            requireActivity(),
            getString(R.string.history_delete_item),
            R.drawable.ic_delete,
            bgColor = requireActivity().getColor(R.color.red),
            textColor = requireActivity().getColor(R.color.white),
        ) { pos ->
            val track = trackAdapter.getItem(pos)!!

            showDialog {
                removeTrack(pos, track.id)
            }
        }
    }
}