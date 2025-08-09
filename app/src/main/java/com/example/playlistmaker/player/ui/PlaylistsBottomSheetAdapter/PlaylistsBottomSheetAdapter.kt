package com.example.playlistmaker.player.ui.PlaylistsBottomSheetAdapter

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
import com.example.playlistmaker.media.domain.model.Playlist
import java.io.File

class PlaylistsBottomSheetAdapter(
    private val onClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistsBottomSheetAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(a: Playlist, b: Playlist) = a.id == b.id
        override fun areContentsTheSame(a: Playlist, b: Playlist) = a == b
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val cover = view.findViewById<ImageView>(R.id.ivCover)
        private val name = view.findViewById<TextView>(R.id.tvName)
        private val count = view.findViewById<TextView>(R.id.tvCount)
        fun bind(item: Playlist) {
            name.text = item.name
            count.text = "${item.trackCount} трек(ов)"
            val path = item.coverPath
            if (!path.isNullOrBlank()) {
                val file = File(path)
                if (file.exists()) {
                    Glide.with(cover)
                        .load(file)
                        .placeholder(R.drawable.zaglyshka)
                        .centerCrop()
                        .into(cover)
                } else {
                    cover.setImageResource(R.drawable.zaglyshka)
                }
            } else {
                cover.setImageResource(R.drawable.zaglyshka)
            }

            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))
}