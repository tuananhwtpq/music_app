package com.example.baseproject.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.baseproject.R
import com.example.baseproject.activities.AddSongActivity
import com.example.baseproject.databinding.FragmentNewPlaylistBinding
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.viewmodel.PLaylistSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewPlaylistFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    val binding get() = _binding!!

    companion object {
        const val TAG = "NewPlaylistFragment"

        fun newInstance() = NewPlaylistFragment()
    }

    private val playlistSharedViewModel: PLaylistSharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreate.setOnClickListener { handleCreatePlaylist() }
        binding.btnCancel.setOnClickListener { dismiss() }

        playlistSharedViewModel.createdPlaylistId.observe(viewLifecycleOwner) { newId ->
            if (newId != null) {
                playlistSharedViewModel.onPlaylistCreationHandled()
                dismiss()
                showToast("Create playlist success")
                //navigate to add song fragment

                navigateToAddSongActivity(newId)
            }
        }
    }

    private fun navigateToAddSongActivity(playlistId: Long) {
        val intent = Intent(requireContext(), AddSongActivity::class.java)
        intent.putExtra("PLAYLIST_ID", playlistId)
        startActivity(intent)
    }

    private fun handleCreatePlaylist() {
        val name = binding.edPlaylistName.text.toString()
        if (name.isNotBlank()) {
            playlistSharedViewModel.createNewPlaylist(name)
        } else {
            showToast("Enter playlist name")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}