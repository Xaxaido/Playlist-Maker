package com.practicum.playlistmaker.player.ui

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.medialibrary.ui.PlaylistsBottomDialogFragment
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.services.MusicService
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {

    companion object {
        const val ARGS_TRACK = "ARGS_TRACK"
        private const val LOG_TAG = "Media Player"

        fun createArgs(json: String): Bundle =
            bundleOf(ARGS_TRACK to json)
    }

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(requireArguments().getString(ARGS_TRACK).orEmpty())
    }
    private lateinit var trackJson: String
    private var cover: Bitmap? = null
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            viewModel.setAudioPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeAudioPlayerControl()
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(LOG_TAG, "Permission 'POST_NOTIFICATIONS' granted!")
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setupUI()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? BackButtonState)?.setIconColor(false)
        unbindMusicService()
    }

    private fun setListeners() {
        binding.btnPlay.setOnClickListener { viewModel.playbackControl() }
        binding.addToFavoriteButton.setOnClickListener { viewModel.addToFavorites() }

        binding.btnAddToPlaylist.setOnClickListener {
            PlaylistsBottomDialogFragment.newInstance(viewModel.track).show(childFragmentManager, PlaylistsBottomDialogFragment.TAG)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerState.collect { state ->
                    renderState(state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trackBufferingState.collect { progress ->
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
        trackJson = requireArguments().getString(ARGS_TRACK).orEmpty()
        viewModel.setTrack()
        bindMusicService()
        binding.shimmerPlaceholder.shimmer.startShimmer()
        updatePlayBtnState(false)
    }

    private fun createServiceIntent(): Intent {
        return Intent(requireContext(), MusicService::class.java).apply {
            putExtra(ARGS_TRACK, trackJson)
        }
    }

    private fun bindMusicService() {
        requireContext().bindService(createServiceIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
    }

    private fun syncScrollingText() {
        val textViews = listOf(binding.playerTrackTitle, binding.playerArtistName)
        val maxDuration = textViews.maxOf { it.scrollDuration }
        textViews.forEach {
            it.scrollDelay += maxDuration - it.scrollDuration
            it.startScrolling()
        }
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
            .asBitmap()
            .load(track.getPlayerAlbumCover())
            .placeholder(R.drawable.player_album_cover_stub)
            .centerCrop()
            .transform(RoundedCorners(8.dpToPx(requireActivity())))
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    cover = resource
                    binding.albumCover.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        syncScrollingText()
    }

    private fun updateCurrentTime(currentPosition: String) {
        binding.currentTime.setTime(currentPosition)
        Log.d(LOG_TAG, "$LOG_TAG $currentPosition")
    }

    private fun stop() {
        binding.btnPlay.redraw(false)
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

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.TrackData -> fillTrackData(state.track)
            is PlayerState.CurrentTime -> updateCurrentTime(state.time)
            is PlayerState.Stop -> stop()
            is PlayerState.Description -> showTrackDescription(state.result)
            is PlayerState.Playing -> updateCover(state.isPlaying)
            is PlayerState.IsFavorite -> binding.addToFavoriteButton.updateBtnState(state.isFavorite, false)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? BackButtonState)?.updateBackBtn(true)
        viewModel.showNotification(false)
    }

    override fun onPause() {
        super.onPause()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                viewModel.showNotification(true, cover)
            }
        } else {
            viewModel.showNotification(true, cover)
        }
    }
}