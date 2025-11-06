package com.example.baseproject.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemGridCardBinding
import com.example.baseproject.models.PlaylistWithTracks

class PlaylistFavorAdapter(
    private val onClick: (PlaylistWithTracks) -> Unit
) : RecyclerView.Adapter<PlaylistFavorAdapter.PlaylistFavorViewHolder>() {

    private val playlistList = mutableListOf<PlaylistWithTracks>()

    inner class PlaylistFavorViewHolder(private val binding: ItemGridCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaylistWithTracks) {
            binding.tvCardTitle.text = item.playlist.name

            val trackCount = item.tracks.size.toString()
            binding.tvCardSubtitle.text = "$trackCount Songs"
            binding.layoutCardView.background =
                getDrawable(binding.root.context, R.drawable.fake_bg_2)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(data: List<PlaylistWithTracks>?) {
        playlistList.clear()
        if (data != null) {
            playlistList.addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistFavorAdapter.PlaylistFavorViewHolder {
        val view = ItemGridCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistFavorViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistFavorAdapter.PlaylistFavorViewHolder,
        position: Int
    ) {
        return holder.bind(playlistList[position])
    }

    override fun getItemCount(): Int = playlistList.size
}