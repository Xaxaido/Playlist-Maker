package com.practicum.playlistmaker.presentation.player

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.data.service.PlaybackServiceInteractor

class PlaybackServiceInteractorImpl(
    private val context: Context,
): PlaybackServiceInteractor {

    override fun getPlaybackPendingIntent(): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, PlayerActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
}