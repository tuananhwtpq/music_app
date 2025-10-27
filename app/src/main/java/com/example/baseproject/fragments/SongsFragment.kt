package com.example.baseproject.fragments

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.adapters.SongAdapter
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentSongsBinding
import com.example.baseproject.models.Song
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.example.baseproject.viewmodel.SongViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

        songAdapter = SongAdapter { song ->
            Log.d(TAG, "Click item: ${song.title}")
            sharedViewModel.selectSong(song)
            sharedViewModel.setPlayerSheetVisibility(true)
            //showPlayerBottomSheet(song)

        }


        binding.rvListSongs.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showPlayerBottomSheet(song: Song) {

        val existingSheet =
            parentFragmentManager.findFragmentByTag(PlayerBottomSheetDialogFragment.TAG)
        if (existingSheet != null) {
            (existingSheet as? BottomSheetDialogFragment)?.dismiss()
        }
        val playerBottomSheet = PlayerBottomSheetDialogFragment.newInstance()

        playerBottomSheet.show(parentFragmentManager, PlayerBottomSheetDialogFragment.TAG)
    }

    override fun initActionView() {

        viewModel.songList.observe(viewLifecycleOwner) { songs ->
            songAdapter.submitList(songs)
        }

    }


}