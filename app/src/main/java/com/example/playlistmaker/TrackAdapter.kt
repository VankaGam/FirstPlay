package com.example.playlistmaker

import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import model.Track
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(
    private var tracks: List<Track>,
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    companion object {
        private var lastClickTime = 0L
        private const val debounceInterval = 1000L
    }

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val trackNameTextView: TextView = view.findViewById(R.id.nameTrack)
        private val artistNameTextView: TextView = view.findViewById(R.id.nameArtist)
        private val trackTimeTextView: TextView = view.findViewById(R.id.timeTrack)
        private val trackImageView: ImageView = view.findViewById(R.id.imageView)

        fun bind(track: Track, onItemClick: (Track) -> Unit) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = track.getFormattedTrackTime()
            itemView.setOnClickListener {
                onItemClick(track)
            }

            itemView.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime >= debounceInterval) {
                    lastClickTime = currentTime
                    onItemClick(track)
                }
            }

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(RoundedCorners(4))
                .into(trackImageView)

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
        holder.bind(tracks[position], onItemClick)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}