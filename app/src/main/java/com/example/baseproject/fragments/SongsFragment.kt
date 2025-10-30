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
                if (sharedViewModel.currentTrackPlaying.value != song) {
                    sharedViewModel.selectSong(song)
                }
                sharedViewModel.setPlayerSheetVisibility(true)
            },
            onTymClicked = { song ->
                val newFavoriteStatus = !song.isFavorite
                viewModel.updateFavoriteStatus(
                    song.mediaStoreId,
                    newFavoriteStatus,
                    sharedViewModel
                )
            },
            onMoreClicked = { song ->
                val trackInfoDialog = TrackInfoFragment.newInstance(song)
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

        binding.layoutPlayBtn.setOnClickListener { handlePlayButton() }
        binding.layoutShuffleBtn.setOnClickListener { handleShuffleButton() }
        binding.layoutSortBy.setOnClickListener { handleSortByButton() }

    }

    private fun handleSortByButton() {
        showToast("Chức năng đang phát triển")
    }

    private fun handleShuffleButton() {
        val trackList = viewModel.trackList.value

        if (trackList.isNullOrEmpty()) {
            showToast("Danh sách bài hát trống")
            return
        }

        val shuffleList = trackList.shuffled()
        val firstTrack = shuffleList[0]
        sharedViewModel.selectSong(firstTrack)
        showToast("Đang phát: ${firstTrack.title}")
    }

    private fun handlePlayButton() {
        val currentTrack = sharedViewModel.currentTrackPlaying.value
        val trackList = viewModel.trackList.value

        if (trackList.isNullOrEmpty()) {
            showToast("Danh sách bài hát trống")
            return
        }

        if (currentTrack == null) {
            val firstTrack = trackList[0]
            sharedViewModel.selectSong(firstTrack)
            showToast("Đang phát: ${firstTrack.title}")

        } else {
            Log.d(TAG, "handlePlayButton: Track is already playing: ${currentTrack.title}")

        }
    }
}