package com.practicum.playlistmaker.data.impl

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.practicum.playlistmaker.data.MediaPlayerListenerAdapter
import com.practicum.playlistmaker.presentation.player.PlaybackService
import com.practicum.playlistmaker.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.Track

class PlayerRepositoryImpl (
    private val context: Context,
) : PlayerRepository {

    private lateinit var controller: ListenableFuture<MediaController>
    private lateinit var mediaPlayer: MediaController
    override val isPlaying get() = mediaPlayer.isPlaying
    override val currentPosition get() = mediaPlayer.currentPosition

    override fun init(stateListener: MediaPlayerListener, track: Track) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))

        controller = MediaController.Builder(context, sessionToken).buildAsync()
        controller.apply {
            addListener({
                if (isDone) {
                    mediaPlayer = get()
                    mediaPlayer.addListener(MediaPlayerListenerAdapter(stateListener))
                    prepareMedia(track)
                }
            }, MoreExecutors.directExecutor())
        }
    }

    private fun prepareMedia(track: Track) {
        val mediaItem = MediaItem.Builder()
            .setUri(track.previewUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist(track.artistName)
                    .setTitle(track.trackName)
                    .setArtworkUri(Uri.parse(track.getPlayerAlbumCover()))
                    .build()
            )
            .build()

        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare()
    }

    override fun play() { mediaPlayer.play() }
    override fun pause() { mediaPlayer.pause() }

    override fun release() {
        mediaPlayer.pause()
        MediaController.releaseFuture(controller)
    }
}