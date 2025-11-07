package com.example.baseproject.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemFolderBinding
import com.example.baseproject.models.Folders

class FolderAdapter(
    val onFolderClick: (Folders) -> Unit,
    val onMoreClick: (Folders) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private val folders = mutableListOf<Folders>()

    inner class FolderViewHolder(private val binding: ItemFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Folders) {

            binding.tvPlaylistName.text = item.folderName

            val trackCount = item.tracks.size
            binding.tvPlaylistSongCount.text = "$trackCount songs"

            Glide.with(binding.root)
                .load(item.firstTrackAlbumUri)
                .placeholder(R.drawable.download)
                .into(binding.ivPlaylistArt)

            binding.root.setOnClickListener { onFolderClick(item) }
            binding.ivMoreOptions.setOnClickListener { onMoreClick(item) }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(data: List<Folders>) {
        folders.clear()
        folders.addAll(data)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FolderAdapter.FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderAdapter.FolderViewHolder, position: Int) {
        return holder.bind(folders[position])
    }

    override fun getItemCount(): Int = folders.size
}