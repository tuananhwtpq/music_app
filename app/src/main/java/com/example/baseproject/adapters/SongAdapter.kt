package com.example.baseproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.SongItemBinding
import com.example.baseproject.models.Track

class SongAdapter(
    private val onSongClick: (Track) -> Unit,
    private val onTymClicked: (Track) -> Unit,
    private val onMoreClicked: (Track) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    companion object {
        const val TAG = "SongAdapter_DEBUG"
    }

    private val tracks = mutableListOf<Track>()

    inner class SongViewHolder(private val binding: SongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Track) {
            binding.tvSongName.text = item.title
            binding.tvSongArtist.text = item.artist
            Glide.with(binding.root.context)
                .load(item.albumArtUri)
                .placeholder(R.drawable.download)
                .into(binding.ivSongImage)

            if (item.isFavorite) {
                binding.btnFavorite.setImageResource(R.drawable.tym_clicked)
            } else {
                binding.btnFavorite.setImageResource(R.drawable.hear_btn_2)
            }

            binding.root.setOnClickListener {
                onSongClick(item)
            }

            binding.btnFavorite.setOnClickListener {
                onTymClicked(item)
            }

            binding.btnMore.setOnClickListener {
                onMoreClicked(item)
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
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun submitList(data: List<Track>) {
        tracks.clear()
        tracks.addAll(data)
        notifyDataSetChanged()
    }
}