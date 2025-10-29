package com.example.baseproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemPlayStackBinding

class PlayStackAdapter(
    val onItemClicked: (MediaItem) -> Unit,
    val onTymClicked: (MediaItem) -> Unit,
    val onDeleteClicked: (MediaItem) -> Unit
) : RecyclerView.Adapter<PlayStackAdapter.PlayStackViewHolder>() {

    private val trackList = mutableListOf<MediaItem>()

    inner class PlayStackViewHolder(private val binding: ItemPlayStackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MediaItem) {
            binding.tvSongName.text = item.mediaMetadata.title

            binding.favoriteBtn.setImageResource(
                if (item.mediaMetadata.extras?.getBoolean("is_favorite") == true) {
                    R.drawable.tym_clicked
                } else {
                    R.drawable.hear_btn_2
                }
            )

            binding.favoriteBtn.setOnClickListener { onTymClicked(item) }
            binding.deleteBtn.setOnClickListener { onDeleteClicked(item) }
            binding.root.setOnClickListener { onItemClicked(item) }
        }
    }

    fun submitData(tracks: List<MediaItem>) {
        trackList.clear()
        trackList.addAll(tracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayStackAdapter.PlayStackViewHolder {
        val view = ItemPlayStackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayStackViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlayStackAdapter.PlayStackViewHolder,
        position: Int
    ) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int = trackList.size
}