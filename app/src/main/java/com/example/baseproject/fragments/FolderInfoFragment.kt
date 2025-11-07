package com.example.baseproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baseproject.databinding.FragmentFolderBinding
import com.example.baseproject.models.Folders
import com.example.baseproject.utils.ex.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FolderInfoFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "FolderInfoFragment"

        fun newInstance(folder: Folders): FolderInfoFragment {

            val args = Bundle()
            args.putParcelable("FOLDER_INFO", folder)


            return FolderInfoFragment().apply {
                arguments = args
            }
        }
    }

    private var currentFolder: Folders? = null
    private var _binding: FragmentFolderBinding? = null
    val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentFolder = arguments?.getParcelable("FOLDER_INFO")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentFolder.let {
            binding.tvSongName.text = it?.folderName
        }

        binding.layoutAddToQueue.setOnClickListener { showToast("Dang phat trien") }
        binding.layoutAddToPlaylist.setOnClickListener { showToast("Dang phat trien") }
        binding.layoutPlayNext.setOnClickListener { showToast("Dang phat trien") }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}