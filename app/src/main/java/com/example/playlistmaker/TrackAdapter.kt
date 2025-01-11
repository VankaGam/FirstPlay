package com.example.playlistmaker

import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import model.Track
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>(), Filterable {

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val trackNameTextView: TextView = view.findViewById(R.id.nameTrack)
        private val artistNameTextView: TextView = view.findViewById(R.id.nameArtist)
        private val trackTimeTextView: TextView = view.findViewById(R.id.timeTrack)
        private val trackImageView: ImageView = view.findViewById(R.id.imageView)

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = track.getFormattedTrackTime()

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(RoundedCorners(4))
                .into(trackImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

}