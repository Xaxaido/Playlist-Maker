package com.practicum.playlistmaker.player.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.player.ui.PlayerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicService : Service(), AudioPlayerControl {

    companion object {
        private const val LOG_TAG = "Music Service"
    }

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default)
    override val playerState = _playerState.asStateFlow()

    private val _trackBufferingState = MutableStateFlow(0)
    override val trackBufferingState: StateFlow<Int> = _trackBufferingState.asStateFlow()

    private val binder = MusicServiceBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var isInForegroundMode = false
    private lateinit var notification: MediaPlayerNotification
    override val isPLaying get() = mediaPlayer?.isPlaying ?: false
    private val timer: Debounce<Any> =
        Debounce(Util.UPDATE_PLAYBACK_PROGRESS_DELAY, CoroutineScope(Dispatchers.Default)) {
            val time = mediaPlayer?.currentPosition?.millisToSeconds() ?: getString(R.string.default_duration_start)
            setState(PlayerState.CurrentTime(time))
            Log.d(LOG_TAG, "$LOG_TAG $time")

            if (isInForegroundMode) {
                notification.updateNotification(
                    true,
                    mediaPlayer?.currentPosition ?: 0,
                    mediaPlayer?.duration ?: 0
                )
            }
        }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isPlaying = mediaPlayer?.isPlaying == true

        when (intent?.action) {
            MediaPlayerNotification.ACTION_PLAY_PAUSE -> {
                if (isPlaying) {
                    pause()
                } else {
                    play()
                }
            }
        }
        notification.updateNotification(!isPlaying, mediaPlayer?.currentPosition ?: 0, mediaPlayer?.duration ?: 0)

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()
        notification = MediaPlayerNotification(this)
        notification.createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        val json = intent?.getStringExtra(PlayerFragment.ARGS_TRACK) ?: ""
        val track = Util.jsonToTrack(json)
        initMediaPlayer(track.previewUrl)
        notification.setTrack(track)

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun play() {
        mediaPlayer?.start()
        setState(PlayerState.Playing(true))
        timer.start(true)
    }

    override fun pause() {
        mediaPlayer?.pause()
        setState(PlayerState.Playing(false))
        timer.stop()
    }

    private fun setState(state: PlayerState) { _playerState.value = state }

    private fun initMediaPlayer(track: String) {
        mediaPlayer?.apply {
            reset()
            setDataSource(track)
            setOnPreparedListener {
                _trackBufferingState.value = 100
                _playerState.value = PlayerState.Prepared
            }
            setOnCompletionListener {
                stopForeground()
                _playerState.value = PlayerState.Stop
            }
            setOnBufferingUpdateListener { _, percent ->
                _trackBufferingState.value = percent
            }
            prepareAsync()
        }
    }

    private fun releasePlayer() {
        timer.stop()
        mediaPlayer?.apply {
            stop()
            _playerState.value = PlayerState.Default
            setOnPreparedListener(null)
            setOnCompletionListener(null)
            setOnBufferingUpdateListener(null)
            release()
        }

        mediaPlayer = null
    }

    override fun startForeground(cover: Bitmap?) {
        isInForegroundMode = true
        notification.setCover(cover)
        ServiceCompat.startForeground(
            this,
            MediaPlayerNotification.SERVICE_NOTIFICATION_ID,
            notification.createNotification(
                mediaPlayer?.isPlaying == true,
                mediaPlayer?.currentPosition ?: 0,
                mediaPlayer?.duration ?: 0
            ),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun stopForeground() {
        isInForegroundMode = false
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
}