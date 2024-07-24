package com.practicum.playlistmaker.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.data.model.entity.Track
import com.practicum.playlistmaker.extension.util.Util
import com.practicum.playlistmaker.extension.util.Util.dpToPx
import com.practicum.playlistmaker.extension.util.Util.millisToSeconds
import com.practicum.playlistmaker.extension.util.Util.toDate

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        IntentCompat.getSerializableExtra(intent, Util.KEY_TRACK, Track::class.java)?.let { track = it }

        with (binding) {
            playerTrackTitle.text = track.trackName
            playerArtistName.text = track.artistName
            playerDurationText.text = track.trackTimeMillis.millisToSeconds()
            yearText.text = track.releaseDate?.toDate() ?: ""
            genreText.text = track.primaryGenreName
            countryText.text = track.country
            albumTitleText.text = track.collectionName
        }

        Glide.with(this)
            .load(track.getPlayerAlbumCover())
            .placeholder(R.drawable.player_album_cover_stub)
            .transform(RoundedCorners(8.dpToPx(this)))
            .into(findViewById(R.id.album_cover))
    }
}