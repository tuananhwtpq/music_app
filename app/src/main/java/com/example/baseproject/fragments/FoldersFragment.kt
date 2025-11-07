package com.example.baseproject.fragments

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.activities.FolderInfoActivity
import com.example.baseproject.adapters.FolderAdapter
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentFoldersBinding
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.viewmodel.FolderViewModel

class FoldersFragment : BaseFragment<FragmentFoldersBinding>(FragmentFoldersBinding::inflate) {

    private val viewModel: FolderViewModel by viewModels()
    private lateinit var folderAdapter: FolderAdapter

    override fun initData() {
        viewModel.loadFolders()
    }

    override fun initView() {
        folderAdapter = FolderAdapter(
            onFolderClick = { folders ->

                val intent = Intent(requireContext(), FolderInfoActivity::class.java)
                intent.putExtra("FOLDER_ID", folders.folderId)
                intent.putExtra("FOLDER_NAME", folders.folderName)
                startActivity(intent)
            },
            onMoreClick = { folders ->

                val folderInfo = FolderInfoFragment.newInstance(folders)
                folderInfo.show(childFragmentManager, FolderInfoFragment.TAG)
            }
        )

        binding.rvFoldersPlaylist.apply {
            adapter = folderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initActionView() {

        viewModel.folders.observe(viewLifecycleOwner) { folders ->
            if (folders.isNotEmpty()) {
                folderAdapter.submitList(folders)
            } else {
                showToast("Folders Empty")
            }
        }
    }

}