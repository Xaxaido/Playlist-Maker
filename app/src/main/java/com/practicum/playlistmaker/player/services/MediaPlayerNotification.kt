package com.practicum.playlistmaker.player.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.search.domain.model.Track

class MediaPlayerNotification(
    private val context: Context,
) {

    companion object {
        const val SERVICE_NOTIFICATION_ID = 100
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        private const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
    }

    private var track: Track? = null
    private var cover: Bitmap? = null
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun setTrack(track: Track) {
        this.track = track
    }

    fun setCover(cover: Bitmap?) {
        this.cover = cover
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.service_name),
            NotificationManager.IMPORTANCE_LOW
        )

        channel.description = context.getString(R.string.service_description)
        channel.setSound(null, null)
        channel.enableVibration(false)
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(isPlaying: Boolean, currentPosition: Int, duration: Int): Notification {
        val icon = if (isPlaying) R.drawable.pause_button else R.drawable.play_button
        val notificationLayout = RemoteViews(context.packageName, R.layout.layout_notification_small)
        val notificationLayoutExpanded = RemoteViews(context.packageName, R.layout.layout_notification_large)

        cover?.let {
            notificationLayoutExpanded.setImageViewBitmap(R.id.notification_icon, it)
        }

        notificationLayout.setTextViewText(R.id.notification_title, context.getString(R.string.app_name))
        notificationLayout.setTextViewText(R.id.notification_text, "${track?.artistName} - ${track?.trackName}")
        notificationLayoutExpanded.setTextViewText(R.id.notification_title, track?.artistName)
        notificationLayoutExpanded.setTextViewText(R.id.notification_text, track?.trackName)
        notificationLayoutExpanded.setTextViewText(R.id.current_time, currentPosition.millisToSeconds())
        notificationLayoutExpanded.setTextViewText(R.id.duration, duration.millisToSeconds())
        notificationLayoutExpanded.setImageViewResource(R.id.notification_play_pause, icon)

        val progress = if (duration > 0) (currentPosition * 100 / duration) else 1
        notificationLayoutExpanded.setProgressBar(R.id.notification_progress, 100, progress, false)

        val playPauseIntent = Intent(context, MusicService::class.java).apply {
            action = "ACTION_PLAY_PAUSE"
        }
        val playPausePendingIntent = PendingIntent.getService(
            context,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationLayoutExpanded.setOnClickPendingIntent(R.id.notification_play_pause, playPausePendingIntent)

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(DecoratedMediaCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
    }

    fun updateNotification(isPlaying: Boolean, currentPosition: Int, duration: Int) {
        notificationManager.notify(SERVICE_NOTIFICATION_ID, createNotification(isPlaying, currentPosition, duration))
    }
}