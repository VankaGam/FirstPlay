package com.example.playlistmaker.playlist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track

class TracksInPlaylistAdapter(
    private val onClick: (Track) -> Unit,
    private val onLongClick: (Track) -> Unit
) : ListAdapter<Track, TracksInPlaylistAdapter.ViewHolder>(Diff) {

    object Diff : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(a: Track, b: Track) = a.trackId == b.trackId
        override fun areContentsTheSame(a: Track, b: Track) = a == b
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val iv = view.findViewById<ImageView>(R.id.imageView)
        private val name = view.findViewById<TextView>(R.id.nameTrack)
        private val artist = view.findViewById<TextView>(R.id.nameArtist)
        private val time = view.findViewById<TextView>(R.id.timeTrack)

        fun bind(item: Track) {
            name.text = item.trackName
            artist.text = item.artistName
            time.text = item.getFormattedTrackTime()

            val url = item.artworkUrl100.ifBlank { null }
            if (url == null) {
                iv.setImageResource(R.drawable.zaglyshka)
            } else {
                Glide.with(iv).load(item.getCoverArtwork())
                    .placeholder(R.drawable.zaglyshka)
                    .error(R.drawable.zaglyshka)
                    .into(iv)
            }

            itemView.setOnClickListener { onClick(item) }
            itemView.setOnLongClickListener { onLongClick(item); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}