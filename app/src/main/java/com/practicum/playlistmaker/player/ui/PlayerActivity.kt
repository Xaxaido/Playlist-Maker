package com.practicum.playlistmaker.player.ui

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(Util.KEY_TRACK)?.also {
            track = viewModel.getTrack(it)
        }

        setListeners()
        setupUI()
    }

    private fun setListeners() {
        viewModel.liveData.observe(this, ::renderState)
        binding.btnPlay.setOnClickListener { viewModel.controlPlayback() }
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUI() {
        binding.shimmerPlaceholder.shimmer.startShimmer()

        binding.albumCover.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                binding.albumCover.viewTreeObserver.removeOnGlobalLayoutListener(this)
                adjustLayout(binding.albumCover.width)
            }
        })

        updatePlayBtnState(false)
        viewModel.searchTrackDescription(track.artistViewUrl)

        with (binding) {
            playerTrackTitle.text = track.trackName
            playerArtistName.text = track.artistName
            playerDurationText.text = track.duration
            yearText.text = track.releaseDate ?: ""
            genreText.text = track.genre
            albumTitleText.text = track.albumName
        }

        Glide.with(this)
            .load(track.getPlayerAlbumCover())
            .placeholder(R.drawable.player_album_cover_stub)
            .transform(RoundedCorners(8.dpToPx(this)))
            .into(binding.albumCover)
    }

    private fun adjustLayout(coverWidth: Int) {
        val screenWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else {
            @Suppress("DEPRECATION")
            DisplayMetrics().let { displayMetrics ->
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.widthPixels
            }
        }

        if (coverWidth < screenWidth * Util.PLAYER_ALBUM_COVER_WIDTH_MULTIPLIER) {
            val scrollView = NestedScrollView(this).apply {
                id = View.generateViewId()
                overScrollMode = View.OVER_SCROLL_NEVER
                layoutParams = binding.expandedContainer.layoutParams
            }

            binding.expandedContainer.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            }

            binding.contentLayout.removeView(binding.expandedContainer)
            scrollView.addView(binding.expandedContainer)
            binding.contentLayout.addView(scrollView)

            ConstraintSet().apply {
                clone(binding.expandedContainer)
                clear(binding.albumCover.id, ConstraintSet.BOTTOM)
                clear(binding.albumTitle.id, ConstraintSet.BOTTOM)
                clear(binding.playerArtistName.id, ConstraintSet.BOTTOM)
                applyTo(binding.expandedContainer)
            }
        }
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
        viewModel.init(track)
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