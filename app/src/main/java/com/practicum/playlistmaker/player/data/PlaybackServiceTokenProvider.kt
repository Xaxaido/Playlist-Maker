package com.practicum.playlistmaker.player.data

import android.content.Context
import androidx.media3.session.SessionToken

interface PlaybackServiceTokenProvider {
    fun getSessionToken(context: Context): SessionToken
}