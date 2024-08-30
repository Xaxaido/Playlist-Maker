package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.utils.Util.dpToPx
import com.practicum.playlistmaker.player.domain.model.TrackParcelable
import com.practicum.playlistmaker.common.utils.DtoConverter.toTrack
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory()
        )[PlayerViewModel::class.java]

        binding.shimmerPlaceholder.shimmer.startShimmer()

        IntentCompat.getParcelableExtra(
            intent,
            Util.KEY_TRACK,
            TrackParcelable::class.java,
        )?.let { track = it.toTrack() }

        setListeners()
        setupUI()
    }

    private fun setListeners() {
        viewModel.liveData.observe(this, ::setState)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnPlay.setOnClickListener { viewModel.controlPlayback() }
    }

    private fun setupUI() {
        binding.albumCover.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                binding.albumCover.viewTreeObserver.removeOnGlobalLayoutListener(this)
                adjustLayout(binding.albumCover.width)
            }
        })

        binding.btnPlay.isEnabled = false
        viewModel.searchTrackDescription(track.artistViewUrl)

        with (binding) {
            playerTrackTitle.text = track.trackName
            playerArtistName.text = track.artistName
            playerDurationText.text = track.trackTimeMillis
            yearText.text = track.releaseDate ?: ""
            genreText.text = track.primaryGenreName
            albumTitleText.text = track.collectionName
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
                    ViewGroup.LayoutParams.WRAP_CONTENT
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

    private fun getTrackDescription(result: TrackDescription) {
        binding.apply {
            countryText.text = result.country
            shimmerPlaceholder.shimmer.stopShimmer()
            shimmerPlaceholder.shimmer.isVisible = false
            trackDescription.isVisible = true
        }
    }

    private fun updateCurrentTime(currentPosition: String) {
        binding.currentTime.text = currentPosition
    }

    private fun ready() { binding.btnPlay.isEnabled = true }
    private fun play() { binding.btnPlay.setImageResource(R.drawable.pause_button) }
    private fun pause() { binding.btnPlay.setImageResource(R.drawable.play_button) }

    private fun stop() {
        binding.btnPlay.setImageResource(R.drawable.play_button)
        binding.currentTime.setText(R.string.default_duration_start)
    }

    private fun setState(state: PlayerState) {
        when (state) {
            is PlayerState.CurrentTime -> updateCurrentTime(state.time)
            is PlayerState.Ready -> ready()
            is PlayerState.Playing -> play()
            is PlayerState.Paused -> pause()
            is PlayerState.Stop -> stop()
            is PlayerState.Description -> getTrackDescription(state.result)
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