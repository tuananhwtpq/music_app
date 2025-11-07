package com.example.baseproject.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.databinding.LibItemGridBinding

class LibAlbumAdapter : RecyclerView.Adapter<LibAlbumAdapter.LibAlbumViewHolder>() {

    inner class LibAlbumViewHolder(private val binding: LibItemGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LibAlbumAdapter.LibAlbumViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: LibAlbumAdapter.LibAlbumViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}