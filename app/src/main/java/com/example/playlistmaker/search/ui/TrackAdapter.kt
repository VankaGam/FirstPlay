package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track

class TrackAdapter(
    private var tracks: List<Track>,
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    companion object {
        private var lastClickTime = 0L
        private const val debounceInterval = 1000L
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.nameTrack)
        private val artistName: TextView = itemView.findViewById(R.id.nameArtist)
        private val trackTime: TextView = itemView.findViewById(R.id.timeTrack)
        private val trackImage: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.getFormattedTrackTime()

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(RoundedCorners(4))
                .into(trackImage)

            itemView.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime >= debounceInterval) {
                    lastClickTime = currentTime
                    onItemClick(track)
                }
            }
        }
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size
}