package com.practicum.playlistmaker.domain.source

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.entity.Track
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.extension.util.Util.dpToPx
import com.practicum.playlistmaker.extension.util.Util.isVisible

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var favoritesList = listOf<Track>()
    private val mDiffer = AsyncListDiffer(this, diffCallback)
    private lateinit var onClickListener: OnRecyclerClickListener

    fun setOnClickListener(onClickListener: OnRecyclerClickListener) {
        this.onClickListener = onClickListener
    }

    fun submitList(list: List<Track>) { mDiffer.submitList(list) }

    fun submitFavorites(list: List<Track>) { favoritesList = list }

    fun getItemAtPos(pos: Int): Track = mDiffer.currentList[pos]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTrackBinding.inflate(inflater, parent, false)

        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount() = mDiffer.currentList.size

    inner class TrackViewHolder(
        private val binding: ItemTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(holder: TrackViewHolder, pos: Int) {
            val track = mDiffer.currentList[pos]

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.album_cover_stub))
                .centerCrop()
                .transform(RoundedCorners(2.dpToPx(itemView.context.applicationContext)))
                .into(binding.cover)

            binding.trackTitle.text = track.trackName
            binding.artistName.text = track.artistName
            binding.duration.text = track.trackTime

            holder.itemView.setOnClickListener { onClickListener.onClick(track) }
        }
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Track>() {

            override fun areItemsTheSame(oldItem: Track, newItem: Track) =
                oldItem.trackName == newItem.trackName

            override fun areContentsTheSame(oldItem: Track, newItem: Track) =
                oldItem.trackName == newItem.trackName && oldItem.artistName == newItem.artistName
        }
    }

    fun interface OnRecyclerClickListener {
        fun onClick(track: Track)
    }
}