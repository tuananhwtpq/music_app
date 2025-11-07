package com.example.baseproject.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemPlayStackBinding

class PlayStackAdapter(
    val onItemClicked: (MediaItem) -> Unit,
    val onTymClicked: (MediaItem) -> Unit,
    val onDeleteClicked: (MediaItem) -> Unit
) : RecyclerView.Adapter<PlayStackAdapter.PlayStackViewHolder>() {
    companion object {
        const val TAG = "PlayStackAdapter"
    }

    private val trackList = mutableListOf<MediaItem>()
    private var curentMediaItem: MediaItem? = null

    inner class PlayStackViewHolder(private val binding: ItemPlayStackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.favoriteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = trackList[position]
                    onTymClicked(item)
                }
            }

            binding.deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = trackList[position]
                    onDeleteClicked(item)
                }
            }

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = trackList[position]
                    onItemClicked(item)
                }
            }
        }


        @SuppressLint("ResourceAsColor")
        fun bind(item: MediaItem) {
            binding.tvSongName.text = item.mediaMetadata.title

            val context = binding.root.context

            if (item.mediaMetadata.extras?.getBoolean("is_favorite") == true) {
                binding.favoriteBtn.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.tym_clicked
                    )
                )
            } else {
                binding.favoriteBtn.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.hear_btn_2
                    )
                )

            }

            if (item.mediaId == curentMediaItem?.mediaId) {
                binding.tvSongName.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else {
                binding.tvSongName.setTextColor(ContextCompat.getColor(context, R.color.black))

            }

//            binding.favoriteBtn.setOnClickListener { onTymClicked(item) }
//            binding.deleteBtn.setOnClickListener { onDeleteClicked(item) }
//            binding.root.setOnClickListener { onItemClicked(item) }
        }
    }

    fun updateItemFavorStatus(playlistId: Long, isFavorite: Boolean) {
        Log.d(TAG, "update item is called")
        val index = trackList.indexOfFirst {
            it.mediaMetadata.extras?.getLong("mediaStoreId", -1L) == playlistId
        }

        Log.d(TAG, "playlistid: $playlistId - Index: $index")

        if (index != -1) {

            val oldItem = trackList[index]

            val newExtras = Bundle(oldItem.mediaMetadata.extras).apply {
                putBoolean("is_favorite", isFavorite)
            }

            val newMetadata = oldItem.mediaMetadata.buildUpon()
                .setExtras(newExtras)
                .build()

            val newItem = oldItem.buildUpon()
                .setMediaMetadata(newMetadata)
                .build()

            trackList[index] = newItem
            notifyItemChanged(index)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(tracks: List<MediaItem>) {
        trackList.clear()
        trackList.addAll(tracks)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentMediaItem(item: MediaItem) {
        curentMediaItem = item
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