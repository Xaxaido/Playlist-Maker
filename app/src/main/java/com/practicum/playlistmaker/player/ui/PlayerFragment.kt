package com.practicum.playlistmaker.player.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {

    companion object {
        private const val ARGS_TRACK = "ARGS_TRACK"

        fun createArgs(track: String): Bundle =
            bundleOf(ARGS_TRACK to track)
    }

    private val viewModel by viewModel<PlayerViewModel>()
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun setupUI() {
        requireArguments().getString(ARGS_TRACK).orEmpty().also {
            track = viewModel.getTrack(it)
        }
        track?.also { viewModel.init(it) }

        binding.shimmerPlaceholder.shimmer.startShimmer()
        updatePlayBtnState(false)
        viewModel.searchTrackDescription(track?.artistViewUrl)

        with (binding) {
            track?.also {
                playerTrackTitle.setString(it.trackName)
                playerArtistName.setString(it.artistName)
                playerDurationText.text = it.duration
                yearText.text = it.releaseDate ?: ""
                genreText.text = it.genre
                albumTitleText.text = it.albumName
            }
        }

        Glide.with(this)
            .load(track?.getPlayerAlbumCover())
            .placeholder(R.drawable.player_album_cover_stub)
            .centerCrop()
            .transform(RoundedCorners(8.dpToPx(requireActivity())))
            .into(binding.albumCover)
        syncScrollingText()
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

    private fun updatePlayBtn(isPlaying: Boolean) {
        startAnimation(binding.btnPlay) { updatePlayBtnIcon(isPlaying) }
        updateCover(isPlaying)
    }

    private fun updatePlayBtnIcon(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(
            if (isPlaying) R.drawable.pause_button
            else R.drawable.play_button
        )
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

    private fun updateCurrentTime(currentPosition: String) {
        binding.currentTime.setTime(currentPosition)
    }

    private fun ready() {
        animateProgressChange(binding.progress.max) {
            binding.progress.isVisible = false
            updatePlayBtnState(true)
        }
    }

    private fun stop() {
        updatePlayBtnIcon(false)
        updateCover(false)
        binding.currentTime.reset()
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
            is PlayerState.Playing -> updatePlayBtn(true)
            is PlayerState.Paused -> updatePlayBtn(false)
            is PlayerState.Stop -> stop()
            is PlayerState.BufferedProgress -> updateBufferedProgress(state.progress)
            is PlayerState.Description -> showTrackDescription(state.result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? BackButtonState)?.updateBackBtn(false)
    }

    override fun onResume() {
        super.onResume()
        (activity as? BackButtonState)?.updateBackBtn(true)
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