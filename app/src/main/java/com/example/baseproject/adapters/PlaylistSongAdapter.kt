package com.example.baseproject.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemPlaylistBinding
import com.example.baseproject.models.PlaylistWithTracks
import com.example.baseproject.utils.ex.setVisible

class PlaylistSongAdapter(
    private val onCLick: (PlaylistWithTracks) -> Unit,
    private val onMoreClick: (PlaylistWithTracks) -> Unit
) : RecyclerView.Adapter<PlaylistSongAdapter.PlaylistSongViewHolder>() {

    private val songLists = mutableListOf<PlaylistWithTracks>()

    inner class PlaylistSongViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaylistWithTracks) {

            binding.tvPlaylistName.text = item.playlist.name

            val count = item.tracks.size

            binding.tvPlaylistSongCount.text = "$count songs"
            Glide.with(binding.root)
                .load(item.playlist.albumArtUri)
                .placeholder(R.drawable.download)
                .into(binding.ivPlaylistArt)

            binding.ivPin.visibility = if (item.playlist.isPin == true) View.VISIBLE else View.GONE

            binding.root.setOnClickListener { onCLick(item) }

            binding.ivMoreOptions.setOnClickListener { onMoreClick(item) }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(list: List<PlaylistWithTracks>?) {
        songLists.clear()
        if (list != null) {
            songLists.addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistSongAdapter.PlaylistSongViewHolder {
        val view = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistSongViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistSongAdapter.PlaylistSongViewHolder,
        position: Int
    ) {
        return holder.bind(songLists[position])
    }

    override fun getItemCount(): Int = songLists.size
}