package com.practicum.playlistmaker.data.service

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.practicum.playlistmaker.Creator

class PlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private var _mediaSession: MediaSession? = null
    private val mediaSession get() = _mediaSession

    private fun getSingleTopActivity() = Creator.getPlaybackServiceInteractor(this)
        .getPlaybackPendingIntent()

    @UnstableApi
    override fun onCreate() {
        super.onCreate()

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                30_000,
                60_000,
                10_000,
                15_000,
            )
            .build()

        player = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .build()

        _mediaSession = MediaSession.Builder(this, player).also { builder ->
            getSingleTopActivity().let { builder.setSessionActivity(it) }
        }.build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        mediaSession?.let {
            if (!it.player.playWhenReady || it.player.mediaItemCount == 0) {
                stopSelf()
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = _mediaSession

    override fun onDestroy() {
        _mediaSession?.run {
            player.release()
            release()
            _mediaSession = null
        }
        super.onDestroy()
    }
}