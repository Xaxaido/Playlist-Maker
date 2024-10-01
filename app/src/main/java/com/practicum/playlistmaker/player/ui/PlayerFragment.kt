package com.practicum.playlistmaker.player.ui

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {

    private val viewModel: PlayerViewModel by activityViewModels()
    private val args: PlayerFragmentArgs by navArgs()
    private var track: Track? = null

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

    private fun setListeners() {
        viewModel.liveData.observe(viewLifecycleOwner, ::renderState)
        binding.btnPlay.setOnClickListener { viewModel.controlPlayback() }

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun setupUI() {
        track = viewModel.getTrack(args.trackJson)

        track?.also {
            viewModel.init(it)
        }


        binding.shimmerPlaceholder.shimmer.startShimmer()
        updatePlayBtnState(false)
        viewModel.searchTrackDescription(track?.artistViewUrl)

        with (binding) {
            playerTrackTitle.text = track?.trackName
            playerArtistName.text = track?.artistName
            playerDurationText.text = track?.duration
            yearText.text = track?.releaseDate ?: ""
            genreText.text = track?.genre
            albumTitleText.text = track?.albumName
        }

        Glide.with(this)
            .load(track?.getPlayerAlbumCover())
            .placeholder(R.drawable.player_album_cover_stub)
            .centerCrop()
            .transform(RoundedCorners(8.dpToPx(requireActivity())))
            .into(binding.albumCover)
    }

    private fun updatePlayBtnState(isActive: Boolean) {
        binding.btnPlay.apply {
            isEnabled = isActive
            alpha = if (isActive) 1f else .5f
        }
    }

    private fun updatePlayBtnIcon(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(
            if (isPlaying) R.drawable.pause_button
            else R.drawable.play_button
        )
    }

    private fun animateProgressChange(value: Int, onAnimationEnd: () -> Unit = {}) {
        ObjectAnimator.ofInt(binding.progress, "progress", value).apply {
            duration = 250
            doOnEnd { onAnimationEnd() }
            start()
        }
    }

    private fun updateCurrentTime(currentPosition: String) {
        binding.currentTime.text = currentPosition
    }

    private fun ready() {
        animateProgressChange(binding.progress.max) {
            binding.progress.isVisible = false
            updatePlayBtnState(true)
        }
    }

    private fun play() { updatePlayBtnIcon(true) }
    private fun pause() { updatePlayBtnIcon(false) }

    private fun stop() {
        updatePlayBtnIcon(false)
        binding.currentTime.setText(R.string.default_duration_start)
    }

    private fun updateBufferedProgress(progress: Int) {
        if (progress == 0) return
        animateProgressChange(progress)
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
            is PlayerState.CurrentTime -> updateCurrentTime(state.time)
            is PlayerState.Ready -> ready()
            is PlayerState.Playing -> play()
            is PlayerState.Paused -> pause()
            is PlayerState.Stop -> stop()
            is PlayerState.BufferedProgress -> updateBufferedProgress(state.progress)
            is PlayerState.Description -> showTrackDescription(state.result)
        }
    }

    override fun onResume() {
        super.onResume()
        track?.also {
            viewModel.init(it)
        }
    }

    override fun onPause() {
        viewModel.release()
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.release()
        super.onDestroy()
    }
}