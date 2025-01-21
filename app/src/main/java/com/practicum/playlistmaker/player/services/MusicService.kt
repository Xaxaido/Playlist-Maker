package com.practicum.playlistmaker.player.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicService : Service(), AudioPlayerControl {

    private companion object {
        const val SERVICE_NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
    }

    private val binder = MusicServiceBinder()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default)
    override val playerState = _playerState.asStateFlow()

    private val _trackBufferingState = MutableStateFlow(0)
    override val trackBufferingState: StateFlow<Int> = _trackBufferingState.asStateFlow()

    var track: Track? = null
    private var mediaPlayer: MediaPlayer? = null
    override val isPLaying get() = mediaPlayer?.isPlaying ?: false
    private val timer: Debounce<Any> =
        Debounce(Util.UPDATE_PLAYBACK_PROGRESS_DELAY, CoroutineScope(Dispatchers.Default)) {
            setState(PlayerState.CurrentTime(getCurrentTime()))
        }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        val json = intent?.getStringExtra(PlayerFragment.ARGS_TRACK) ?: ""
        track = Util.jsonToTrack(json)
        track?.let {
            initMediaPlayer()
        }
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

    private fun getCurrentTime(): String {
        return mediaPlayer?.currentPosition?.millisToSeconds() ?: "00:00"
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

    private fun setState(state: PlayerState) { _playerState.value = state }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun startForeground() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("${track?.artistName} - ${track?.trackName}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun initMediaPlayer() {
        mediaPlayer?.apply {
            reset()
            setDataSource(track?.previewUrl)
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

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
}
