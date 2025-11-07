package com.example.baseproject.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemAddSongBinding
import com.example.baseproject.models.Track

class AddSongAdapter(
    private val onTrackClicked: (Track, Boolean) -> Unit,
) : RecyclerView.Adapter<AddSongAdapter.AddSongViewHolder>() {

    private val tracks = mutableListOf<Track>()
    private val selectedTrackIds = mutableSetOf<Long>()

    inner class AddSongViewHolder(private val binding: ItemAddSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.tvSongName.text = track.title
            binding.tvSongArtist.text = track.artist

            Glide.with(binding.root.context)
                .load(track.albumArtUri)
                .placeholder(R.drawable.download)
                .into(binding.ivSongArt)

            val isSelected = selectedTrackIds.contains(track.mediaStoreId)
            binding.checkboxAdd.isChecked = isSelected

            binding.root.setOnClickListener {
                toggleSelection(track)
            }
        }

        private fun toggleSelection(track: Track) {
            val isSelected: Boolean
            if (selectedTrackIds.contains(track.mediaStoreId)) {
                selectedTrackIds.remove(track.mediaStoreId)
                isSelected = false
            } else {
                selectedTrackIds.add(track.mediaStoreId)
                isSelected = true
            }
            binding.checkboxAdd.isChecked = isSelected
            onTrackClicked(track, isSelected)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun toggleSelectAll(selectAll: Boolean) {
        if (selectAll) {
            tracks.forEach { selectedTrackIds.add(it.mediaStoreId) }
        } else {
            selectedTrackIds.clear()
        }
        notifyDataSetChanged()
    }

    fun getSelectedTracks(): List<Track> {
        return tracks.filter { selectedTrackIds.contains(it.mediaStoreId) }
    }

    fun getSelectedCount(): Int {
        return selectedTrackIds.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddSongAdapter.AddSongViewHolder {
        val binding = ItemAddSongBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AddSongViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AddSongAdapter.AddSongViewHolder,
        position: Int
    ) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size


}