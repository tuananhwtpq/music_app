package com.example.baseproject.adapters

import android.annotation.SuppressLint
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

            Log.d(
                TAG,
                "bind: Current Media Item in ViewHolder: ${curentMediaItem?.mediaMetadata?.title}"
            )

            if (item.mediaId == curentMediaItem?.mediaId) {
                binding.tvSongName.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else {
                binding.tvSongName.setTextColor(ContextCompat.getColor(context, R.color.black))

            }

            binding.favoriteBtn.setOnClickListener { onTymClicked(item) }
            binding.deleteBtn.setOnClickListener { onDeleteClicked(item) }
            binding.root.setOnClickListener { onItemClicked(item) }
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