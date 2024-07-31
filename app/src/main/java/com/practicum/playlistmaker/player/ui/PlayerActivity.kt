package com.practicum.playlistmaker.player.ui

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.common.util.concurrent.ListenableFuture
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.data.model.entity.Track
import com.practicum.playlistmaker.extension.util.Util
import com.practicum.playlistmaker.extension.util.Util.dpToPx
import com.practicum.playlistmaker.extension.util.Util.millisToSeconds
import com.practicum.playlistmaker.extension.util.Util.toDate
import com.practicum.playlistmaker.player.PlaybackService
import androidx.media3.common.Player
import com.google.common.util.concurrent.MoreExecutors
import com.practicum.playlistmaker.extension.util.Debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track
    private lateinit var controller: ListenableFuture<MediaController>
    private lateinit var mediaPlayer: MediaController
    private val timer: Debounce by lazy {
        Debounce { updateProgress(true) }
    }
    private val isPlaying get() = mediaPlayer.isPlaying
    private val duration get() = mediaPlayer.duration
    private val currentPosition get() = mediaPlayer.currentPosition
    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            when (playbackState) {
                Player.STATE_ENDED -> updateProgress(false)
                else -> {}
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayBtn(isPlaying)
            updateTimer(isPlaying)
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
        setListeners()
        setupUI()
        initPlayer()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnPlay.setOnClickListener {
            mediaPlayer.apply { if (playWhenReady) pause() else play() }
        }
    }

    private fun setupUI() {
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

    private fun initPlayer() {
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))

        controller = MediaController.Builder(this, sessionToken).buildAsync()
        controller.apply {
            addListener({
                if (isDone) {
                    mediaPlayer = get()
                    mediaPlayer.addListener(playerListener)
                    prepareMedia()
                }
            }, MoreExecutors.directExecutor())
        }
    }

    private fun prepareMedia() {
        val mediaItem = MediaItem.Builder()
            .setUri(track.previewUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist(track.artistName)
                    .setTitle(track.trackName)
                    .setArtworkUri(Uri.parse(track.getPlayerAlbumCover()))
                    .build()
            )
            .build()

        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare()
    }

    private fun updateProgress(isPlaying: Boolean) {
        binding.currentTime.text = if (isPlaying) (duration - currentPosition).millisToSeconds() else getString(R.string.default_duration_end)
    }

    private fun updatePlayBtn(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(if (isPlaying) R.drawable.pause_button else R.drawable.play_button)
    }

    private fun updateTimer(isPlaying: Boolean) {
        if (!isPlaying && timer.isRunning) {
            timer.stop()
        } else if (isPlaying) {
            timer.start(true)
        }
    }

    override fun onDestroy() {
        updateTimer(isPlaying)
        mediaPlayer.pause()
        MediaController.releaseFuture(controller)
        super.onDestroy()
    }
}