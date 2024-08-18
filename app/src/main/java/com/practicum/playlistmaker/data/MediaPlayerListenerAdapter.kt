package com.practicum.playlistmaker.data

import androidx.media3.common.Player
import com.practicum.playlistmaker.domain.api.MediaPlayerListener

class MediaPlayerListenerAdapter(
    private val listener: MediaPlayerListener
) : Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        listener.onPlaybackStateChanged(playbackState)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        listener.onIsPlayingChanged(isPlaying)
    }
}