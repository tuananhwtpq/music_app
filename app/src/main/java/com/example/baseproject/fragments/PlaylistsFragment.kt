package com.example.baseproject.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.R
import com.example.baseproject.activities.PlaylistInfoActivity
import com.example.baseproject.adapters.PlaylistFavorAdapter
import com.example.baseproject.adapters.PlaylistSongAdapter
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentPlaylistsBinding
import com.example.baseproject.service.MyPlaybackService
import com.example.baseproject.viewmodel.PLaylistsViewModel

class PlaylistsFragment :
    BaseFragment<FragmentPlaylistsBinding>(FragmentPlaylistsBinding::inflate) {

    private lateinit var playlistFavortAdapter: PlaylistFavorAdapter
    private lateinit var playlistSongAdapter: PlaylistSongAdapter

    private val viewModel: PLaylistsViewModel by viewModels()

    companion object {
        const val TAG = "PlaylistsFragment"
    }

    override fun initData() {

        playlistFavortAdapter = PlaylistFavorAdapter(
            onClick = { playlistWithTracks ->
//                val intent = Intent(requireContext(), PlaylistInfoActivity::class.java)
//                intent.putExtra("PLAYLIST_ID", playlistWithTracks.playlist.playListId)
//                startActivity(intent)
            }
        )

        playlistSongAdapter = PlaylistSongAdapter(
            onCLick = { playlistWithTracks ->
                val intent = Intent(requireContext(), PlaylistInfoActivity::class.java)
                intent.putExtra("PLAYLIST_ID", playlistWithTracks.playlist.playListId)
                startActivity(intent)
            },
            onMoreClick = { item ->
            }
        )


    }

    override fun initView() {

        binding.rvGridCategories.apply {
            adapter = playlistFavortAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        binding.rvPlaylists.apply {
            adapter = playlistSongAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initActionView() {

        binding.btnAddPlaylist.setOnClickListener {
            val newPlaylistFragment = NewPlaylistFragment.newInstance()
            newPlaylistFragment.show(childFragmentManager, NewPlaylistFragment.TAG)
        }

        viewModel.smartPlaylists.observe(viewLifecycleOwner) { smartPlaylists ->
            if (smartPlaylists.isNullOrEmpty()) {
                Log.d(TAG, "No smart playlists found.")
                playlistFavortAdapter.submitData(emptyList())
            } else {
                Log.d(TAG, "Smart playlists found: ${smartPlaylists.size}")
                playlistFavortAdapter.submitData(smartPlaylists)
            }
        }

        viewModel.userPlaylists.observe(viewLifecycleOwner) { userPlaylists ->

            if (userPlaylists.isNullOrEmpty()) {
                Log.d(TAG, "No user-created playlists found.")
                playlistSongAdapter.submitData(emptyList())
            } else {
                Log.d(TAG, "User playlists found: ${userPlaylists.size}")
                playlistSongAdapter.submitData(userPlaylists)
            }
        }

    }

}