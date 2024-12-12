package com.practicum.playlistmaker.player.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.common.utils.MySnackBar
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.common.widgets.recycler.ItemAnimator
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistListItem
import com.practicum.playlistmaker.medialibrary.ui.recycler.PlaylistAdapter
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {

    companion object {
        private const val ARGS_TRACK = "ARGS_TRACK"

        fun createArgs(track: String): Bundle =
            bundleOf(ARGS_TRACK to track)
    }

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(requireArguments().getString(ARGS_TRACK).orEmpty())
    }
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var shouldOpenCreatePlaylistFragment = false
    private var isPlaylisted = false

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (nextAnim == 0) {
            super.onCreateAnimation(transit, enter, nextAnim)
        } else {
            AnimationUtils.loadAnimation(requireActivity(), nextAnim).apply {
                setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        setupUI()
                        setListeners()
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? BackButtonState)?.setIconColor(false)
    }

    private fun setListeners() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout).apply {
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
                    if (shouldOpenCreatePlaylistFragment && binding.overlay.alpha == 0f) {
                        shouldOpenCreatePlaylistFragment = false
                        findNavController().navigate(R.id.action_create_playlist)
                    }
                } catch (_: Exception) {

                }
            }
        })

        binding.createPlaylistBtn.setOnClickListener {
            shouldOpenCreatePlaylistFragment = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnPlay.setOnClickListener { viewModel.controlPlayback() }
        binding.addToFavoriteButton.setOnClickListener { viewModel.addToFavorites() }

        binding.btnAddToPlaylist.setOnClickListener {
            updateBtnState(
                binding.btnAddToPlaylist,
                isPlaylisted,
                R.drawable.added_false_icon,
                R.drawable.added_false_icon,
                true
            )
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { state ->
                    renderState(state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistsFlow.collect { playlists ->
                    showPlaylists(playlists)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trackBufferingFlow.collect { progress ->
                    updateBufferedProgress(progress)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun setupUI() {
        (activity as? BackButtonState)?.setIconColor(true)
        viewModel.setTrack()
        binding.shimmerPlaceholder.shimmer.startShimmer()
        updatePlayBtnState(false)

        playlistAdapter = PlaylistAdapter { playlist ->
            viewModel.addToPlaylist(playlist)
        }
        binding.playlistsRecycler.adapter = playlistAdapter
        binding.playlistsRecycler.itemAnimator = ItemAnimator()
    }

    private fun syncScrollingText() {
        val textViews = listOf(binding.playerTrackTitle, binding.playerArtistName)
        val maxDuration = textViews.maxOf { it.scrollDuration }
        textViews.forEach {
            it.scrollDelay += maxDuration - it.scrollDuration
            it.startScrolling()
        }
    }

    private fun startAnimation(view: View, action: () -> Unit) {
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        scaleDown.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                action()
                view.startAnimation(scaleUp)
            }
        })

        view.startAnimation(scaleDown)
    }

    private fun updatePlayBtnState(isActive: Boolean) {
        binding.btnPlay.apply {
            isEnabled = isActive
            alpha = if (isActive) 1f else .5f
        }
    }

    private fun updateCover(isPlaying: Boolean) {
        val animatorSet = AnimatorSet()
        val view = binding.albumCover

        if (isPlaying) {
            val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1.1f).apply { duration = 150 }
            val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1.1f).apply { duration = 150 }
            val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f).apply { duration = 100 }
            val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f).apply { duration = 100 }

            animatorSet.apply {
                play(scaleUpX).with(scaleUpY)
                play(scaleDownX).with(scaleDownY).after(150)
            }
        } else {
            val restoreScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f).apply { duration = 250 }
            val restoreScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f).apply { duration = 250 }

            animatorSet.play(restoreScaleX).with(restoreScaleY)
        }

        animatorSet.start()
    }

    private fun animateProgressChange(value: Int, onAnimationEnd: () -> Unit = {}) {
        ObjectAnimator.ofInt(binding.progress, "progress", value).apply {
            duration = 250
            doOnEnd { onAnimationEnd() }
            start()
        }
    }

    private fun fillTrackData(track: Track) {
        viewModel.searchTrackDescription(track.artistViewUrl)

        with (binding) {
            playerTrackTitle.setString(track.trackName)
            playerArtistName.setString(track.artistName)
            playerDurationText.text = track.duration.millisToSeconds()
            yearText.text = track.releaseDate ?: ""
            genreText.text = track.genre
            albumTitleText.text = track.albumName
        }

        Glide.with(this)
            .load(track.getPlayerAlbumCover())
            .placeholder(R.drawable.player_album_cover_stub)
            .centerCrop()
            .transform(RoundedCorners(8.dpToPx(requireActivity())))
            .into(binding.albumCover)

        syncScrollingText()
    }

    private fun updateCurrentTime(currentPosition: String) {
        binding.currentTime.setTime(currentPosition)
    }

    private fun stop() {
        updateBtnIcon(binding.btnPlay, false, R.drawable.pause_button, R.drawable.play_button)
        updateCover(false)
        binding.currentTime.reset()
    }

    private fun updateBufferedProgress(progress: Int) {
        if (progress == 0) return

        if (progress < binding.progress.max) {
            animateProgressChange(progress)
        } else {
            animateProgressChange(binding.progress.max) {
                binding.progress.isVisible = false
                updatePlayBtnState(true)
            }
        }
    }

    private fun showTrackDescription(result: TrackDescription) {
        val country = result.country ?: getString(R.string.player_unknown)

        binding.apply {
            shimmerPlaceholder.shimmer.stopShimmer()
            shimmerPlaceholder.shimmer.isVisible = false
            trackDescription.isVisible = true
            countryText.text = country
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        val items = playlistAdapter.convertToPlaylistListItem(PlaylistListItem.PlaylistBottomSheetItem::class.java, playlists)
        playlistAdapter.submitTracksList(items)
    }

    private fun showAddToPlaylistStatus(playlistTitle: String, isAdded: Boolean) {
        val message = if (isAdded) {
            resources.getText(R.string.add_to_playlist_success)
        } else {
            resources.getText(R.string.add_to_playlist_error)
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        MySnackBar(requireActivity(), "$message $playlistTitle").show()
    }

    private fun updateBtnState(
        button: ImageView,
        isInActiveState: Boolean,
        activeIcon: Int,
        inactiveIcon: Int,
        shouldPlayAnimation: Boolean = true,
        action: () -> Unit = {},
    ) {
        if (shouldPlayAnimation) {
            startAnimation(button) {
                updateBtnIcon(button, isInActiveState, activeIcon, inactiveIcon)
                action()
            }
        } else {
            updateBtnIcon(button, isInActiveState, activeIcon, inactiveIcon)
        }
    }

    private fun updateBtnIcon(button: ImageView, isInActiveState: Boolean, activeIcon: Int, inactiveIcon: Int) {
        button.setImageResource(
            if (isInActiveState) activeIcon
            else inactiveIcon
        )
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.TrackData -> fillTrackData(state.track)
            is PlayerState.CurrentTime -> updateCurrentTime(state.time)
            is PlayerState.Stop -> stop()
            is PlayerState.Description -> showTrackDescription(state.result)
            is PlayerState.AddToPlaylist -> showAddToPlaylistStatus(state.playlistTitle, state.isAdded)
            is PlayerState.IsPlaying -> {
                updateBtnState(
                    binding.btnPlay,
                    state.isPlaying,
                    R.drawable.pause_button,
                    R.drawable.play_button
                ) { updateCover(state.isPlaying) }
            }
            is PlayerState.IsFavorite -> {
                updateBtnState(
                    binding.addToFavoriteButton,
                    state.isFavorite,
                    R.drawable.favorite_true_icon,
                    R.drawable.favorite_false_icon,
                    state.shouldPlayAnimation
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? BackButtonState)?.updateBackBtn(true)
    }

    override fun onPause() {
        super.onPause()

        if (!requireActivity().isChangingConfigurations) {
            viewModel.controlPlayback(false)
        }
    }
}