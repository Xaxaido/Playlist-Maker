package com.practicum.playlistmaker.player.data.impl

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.SessionToken
import com.practicum.playlistmaker.player.data.PlaybackServiceTokenProvider
import com.practicum.playlistmaker.player.ui.PlaybackService

class PlaybackServiceTokenProviderImpl : PlaybackServiceTokenProvider {

    override fun getSessionToken(context: Context) =
        SessionToken(context, ComponentName(context, PlaybackService::class.java))
}