package com.practicum.playlistmaker.player.data.impl

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.MediaPlayerListenerAdapter
import com.practicum.playlistmaker.player.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionToken: SessionToken,
    private val gson: Gson,
) : PlayerRepository {

    private lateinit var controller: ListenableFuture<MediaController>
    private var mediaPlayer: MediaController? = null
    override val isPlaying get() = mediaPlayer?.isPlaying ?: false
    override val currentPosition get() = mediaPlayer?.currentPosition ?: -1
    override val bufferedProgress get() = mediaPlayer?.bufferedPercentage ?: 0

    override fun jsonToTrack(json: String): Track = gson.fromJson(json, Track::class.java)

    override fun init(stateListener: MediaPlayerListener, track: Track) {
        controller = MediaController.Builder(context, sessionToken).buildAsync()
        controller.apply {
            addListener({
                if (isDone) {
                    mediaPlayer = get()
                    mediaPlayer?.addListener(MediaPlayerListenerAdapter(stateListener))
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

        mediaPlayer?.setMediaItem(mediaItem)
        mediaPlayer?.prepare()
    }

    override fun play() { mediaPlayer?.play() }
    override fun pause() { mediaPlayer?.pause() }

    override fun release() {
        mediaPlayer?.pause()
        MediaController.releaseFuture(controller)
    }
}