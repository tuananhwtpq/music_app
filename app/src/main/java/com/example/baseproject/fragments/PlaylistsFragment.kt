package com.example.baseproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.adapters.PlaylistFavorAdapter
import com.example.baseproject.adapters.PlaylistSongAdapter
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentPlaylistsBinding
import com.example.baseproject.viewmodel.PLaylistsViewModel

class PlaylistsFragment :
    BaseFragment<FragmentPlaylistsBinding>(FragmentPlaylistsBinding::inflate) {

    private lateinit var playlistFavortAdapter: PlaylistFavorAdapter
    private lateinit var playlistSongAdapter: PlaylistSongAdapter

    private val viewModel: PLaylistsViewModel by viewModels()


    override fun initData() {

        playlistFavortAdapter = PlaylistFavorAdapter(
            onClick = {}
        )

        playlistSongAdapter = PlaylistSongAdapter(
            onCLick = {},
            onMoreClick = {}
        )
    }

    override fun initView() {
    }

    override fun initActionView() {

        binding.btnAddPlaylist.setOnClickListener {

            val newPlaylistFragment = NewPlaylistFragment.newInstance()
            newPlaylistFragment.show(childFragmentManager, NewPlaylistFragment.TAG)
        }
    }

}