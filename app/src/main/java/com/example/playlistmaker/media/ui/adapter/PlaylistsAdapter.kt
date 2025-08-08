package com.example.playlistmaker.media.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.media.domain.model.Playlist
import java.io.File

class PlaylistsAdapter(
    private val onClick: ((Playlist) -> Unit)? = null
) : ListAdapter<Playlist, PlaylistsAdapter.VH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemPlaylistBinding,
        private val onClick: ((Playlist) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Playlist) {
            // Имя
            binding.tvName.text = item.name
            // Кол-во треков с правильным словом
            binding.tvCount.text = formatTracksCount(item.trackCount)

            // Обложка
            val path = item.coverPath
            if (path.isNullOrBlank()) {
                binding.ivCover.setImageResource(R.drawable.zaglyshka)
            } else {
                val file = File(path)
                if (file.exists()) {
                    binding.ivCover.setImageURI(Uri.fromFile(file))
                } else {
                    binding.ivCover.setImageResource(R.drawable.zaglyshka)
                }
            }

            // Клик по карточке (опционально)
            binding.root.setOnClickListener { onClick?.invoke(item) }
        }

        private fun formatTracksCount(c: Int): String {
            val rem100 = c % 100
            val rem10 = c % 10
            val word = when {
                rem100 in 11..19 -> "треков"
                rem10 == 1 -> "трек"
                rem10 in 2..4 -> "трека"
                else -> "треков"
            }
            return "$c $word"
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist) =
            oldItem == newItem
    }
}