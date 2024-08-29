package com.practicum.playlistmaker.player.data

import androidx.media3.common.Player
import com.practicum.playlistmaker.player.domain.api.MediaPlayerListener

class MediaPlayerListenerAdapter(
    private val listener: MediaPlayerListener
) : Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        listener.onPlaybackStateChanged(playbackState)
    }
}