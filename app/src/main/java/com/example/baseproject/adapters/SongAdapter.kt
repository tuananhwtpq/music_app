package com.example.baseproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.SongItemBinding
import com.example.baseproject.models.Song

class SongAdapter(
    private val onSongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private val songs = mutableListOf<Song>()

    inner class SongViewHolder(private val binding: SongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Song) {
            binding.tvSongName.text = item.title
            binding.tvSongArtist.text = item.artist
            Glide.with(binding.root.context)
                .load(item.albumArtUri)
                .placeholder(R.drawable.download)
                .into(binding.ivSongImage)

            binding.root.setOnClickListener {
                onSongClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SongAdapter.SongViewHolder {
        val view = SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongAdapter.SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    fun submitList(data: List<Song>) {
        songs.clear()
        songs.addAll(data)
        notifyDataSetChanged()
    }
}