package com.practicum.playlistmaker.data.service

import android.app.PendingIntent

interface PlaybackServiceInteractor {
    fun getPlaybackPendingIntent(): PendingIntent
}