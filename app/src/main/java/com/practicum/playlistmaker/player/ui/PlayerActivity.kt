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
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.data.domain.model.Track
import com.practicum.playlistmaker.extension.util.Util
import com.practicum.playlistmaker.extension.util.Util.dpToPx
import com.practicum.playlistmaker.extension.util.Util.millisToSeconds
import com.practicum.playlistmaker.extension.util.Util.toDate
import com.practicum.playlistmaker.player.PlayerUI
import com.practicum.playlistmaker.player.PlayerPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class PlayerActivity : AppCompatActivity(), PlayerUI {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerPresenter
    private lateinit var track: Track
    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            when (playbackState) {
                Player.STATE_READY -> binding.btnPlay.isEnabled = true
                Player.STATE_ENDED -> viewModel.updateProgress()
                else -> {}
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayBtn(isPlaying)
            viewModel.updateTimer(isPlaying)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.shimmerPlaceholder.shimmer.startShimmer()
        IntentCompat.getSerializableExtra(
            intent,
            Util.KEY_TRACK,
            Track::class.java,
        )?.let { track = it }
        viewModel = PlayerPresenter(this)
        setListeners()
        setupUI()
        viewModel.init(playerListener, track)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnPlay.setOnClickListener { viewModel.controlPlayback() }
    }

    private fun setupUI() {
        binding.albumCover.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                binding.albumCover.viewTreeObserver.removeOnGlobalLayoutListener(this)
                //adjustLayout(binding.albumCover.width)
            }
        })

        binding.btnPlay.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                Jsoup.connect(track.artistViewUrl).get()
                    .select("dd[data-testid=grouptext-section-content]").firstOrNull()
            }
            binding.apply {
                countryText.text = result?.text()?.substringAfter(",") ?: getString(R.string.player_unknown)
                shimmerPlaceholder.shimmer.stopShimmer()
                shimmerPlaceholder.shimmer.isVisible = false
                trackDescription.isVisible = true
            }
        }

        with (binding) {
            playerTrackTitle.text = track.trackName
            playerArtistName.text = track.artistName
            playerDurationText.text = track.trackTimeMillis.millisToSeconds()
            yearText.text = track.releaseDate?.toDate() ?: ""
            genreText.text = track.primaryGenreName
            albumTitleText.text = track.collectionName
        }

        Glide.with(this)
            .load(track.getPlayerAlbumCover())
            .placeholder(R.drawable.player_album_cover_stub)
            .transform(RoundedCorners(8.dpToPx(this)))
            .into(findViewById(R.id.album_cover))
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

    override fun updatePlayBtn(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(if (isPlaying) R.drawable.pause_button else R.drawable.play_button)
    }

    override fun setProgress(progress: String) { binding.currentTime.text = progress }

    override fun onDestroy() {
        viewModel.release()
        super.onDestroy()
    }
}