package com.practicum.playlistmaker.presentation.player

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
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.Util
import com.practicum.playlistmaker.common.Util.dpToPx
import com.practicum.playlistmaker.presentation.models.TrackParcelable
import com.practicum.playlistmaker.common.DtoConverter.toTrack
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.domain.api.TrackDescriptionInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TrackDescription
import com.practicum.playlistmaker.presentation.utils.Debounce

class PlayerActivity : AppCompatActivity(), PlayerUI {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var presenter: PlayerPresenter
    private lateinit var track: Track
    private val playerListener = object : MediaPlayerListener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> binding.btnPlay.isEnabled = true
                Player.STATE_ENDED -> presenter.updateProgress()
                else -> {}
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayBtn(isPlaying)
            presenter.updateTimer(isPlaying)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.shimmerPlaceholder.shimmer.startShimmer()

        IntentCompat.getParcelableExtra(
            intent,
            Util.KEY_TRACK,
            TrackParcelable::class.java,
        )?.let { track = it.toTrack() }

        presenter = PlayerPresenter(this)
        setListeners()
        setupUI()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnPlay.setOnClickListener { presenter.controlPlayback() }
    }

    private fun setupUI() {
        binding.albumCover.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                binding.albumCover.viewTreeObserver.removeOnGlobalLayoutListener(this)
                adjustLayout(binding.albumCover.width)
            }
        })

        binding.btnPlay.isEnabled = false
        searchTrackDescription(track.artistViewUrl)

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

    private fun searchTrackDescription(url: String) {
        Creator.getTrackDescriptionInteractor(this).searchTrackDescription(url, object : TrackDescriptionInteractor.TracksDescriptionConsumer {

            override fun consume(result: TrackDescription) {
                Debounce(delay = 0L) {
                    binding.apply {
                        countryText.text = result.country
                        shimmerPlaceholder.shimmer.stopShimmer()
                        shimmerPlaceholder.shimmer.isVisible = false
                        trackDescription.isVisible = true
                    }
                }.start()
            }
        })
    }


    override fun updatePlayBtn(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(if (isPlaying) R.drawable.pause_button else R.drawable.play_button)
    }

    override fun setProgress(progress: String) { binding.currentTime.text = progress }

    override fun onResume() {
        super.onResume()
        presenter.init(playerListener, track)
    }

    override fun onPause() {
        presenter.release()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.release()
        super.onDestroy()
    }
}