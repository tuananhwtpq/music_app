package com.example.baseproject.fragments

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.adapters.SongAdapter
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentSongsBinding
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.example.baseproject.viewmodel.SongViewModel

class SongsFragment : BaseFragment<FragmentSongsBinding>(FragmentSongsBinding::inflate) {

    companion object {
        private const val TAG = "SongsFragment"
    }

    private lateinit var songAdapter: SongAdapter
    private val viewModel: SongViewModel by viewModels()
    private val sharedViewModel: MusicSharedViewModel by activityViewModels()


    override fun initData() {
        viewModel.loadSongs()
    }

    override fun initView() {

        songAdapter = SongAdapter(
            onSongClick = { song ->
                Log.d(TAG, "Click item: ${song.title}")
                if (sharedViewModel.currentTrackPlaying.value != song) {
                    sharedViewModel.selectSong(song)
                }
                sharedViewModel.setPlayerSheetVisibility(true)
            },
            onTymClicked = { song ->
                showToast("Tym bài hát ${song.title} ")
            },
            onMoreClicked = { song ->
                val trackInfoDialog = TrackInfoFragment.newInstance()
                trackInfoDialog.show(childFragmentManager, TrackInfoFragment.TAG)
            }
        )




        binding.rvListSongs.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initActionView() {

        viewModel.trackList.observe(viewLifecycleOwner) { songs ->
            songAdapter.submitList(songs)
        }
    }
}