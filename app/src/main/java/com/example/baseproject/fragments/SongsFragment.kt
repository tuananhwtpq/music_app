package com.example.baseproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.R
import com.example.baseproject.adapters.SongAdapter
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentSongsBinding
import com.example.baseproject.viewmodel.SongViewModel

class SongsFragment : BaseFragment<FragmentSongsBinding>(FragmentSongsBinding::inflate) {

    companion object {
        private const val TAG = "SongsFragment"
    }

    private lateinit var songAdapter: SongAdapter
    private val viewModel: SongViewModel by viewModels()


    override fun initData() {
        viewModel.loadSongs()
    }

    override fun initView() {

        songAdapter = SongAdapter { song ->
            Log.d(TAG, "Click item: ${song.title}")
        }

        binding.rvListSongs.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initActionView() {

        viewModel.songList.observe(viewLifecycleOwner) { songs ->
            songAdapter.submitList(songs)
        }

    }


}