package com.example.baseproject.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.R
import com.example.baseproject.adapters.AddSongAdapter
import com.example.baseproject.bases.BaseActivity
import com.example.baseproject.databinding.ActivityAddSongBinding
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.viewmodel.PLaylistSharedViewModel

class AddSongActivity : BaseActivity<ActivityAddSongBinding>(ActivityAddSongBinding::inflate) {

    private val sharedViewModel: PLaylistSharedViewModel by viewModels()
    private lateinit var addSongAdapter: AddSongAdapter
    private var newPlaylistId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newPlaylistId = intent.getLongExtra("PLAYLIST_ID", -1L)

        if (newPlaylistId == -1L) {
            showToast("Lỗi: Không tìm thấy playlist.")
            finish()
            return
        }
    }

    override fun initData() {
        sharedViewModel.loadAllTracksForAdding()
    }

    override fun initView() {
        addSongAdapter = AddSongAdapter(
            onTrackClicked = { track, isSelected ->
                updateToolbarTitle()
                updateSelectAllCheckbox()
            }
        )

        binding.rvAddSongs.apply {
            adapter = addSongAdapter
            layoutManager = LinearLayoutManager(this@AddSongActivity)
        }
    }

    override fun initActionView() {

        binding.backBtn.setOnClickListener { finish() }

        sharedViewModel.allTracks.observe(this) { tracks ->
            addSongAdapter.submitList(tracks)
        }

        binding.btnDone.setOnClickListener {
            val selectedTracks = addSongAdapter.getSelectedTracks()
            if (selectedTracks.isNotEmpty()) {
                sharedViewModel.addSongsToPlaylist(newPlaylistId, selectedTracks)
                showToast("Đã thêm ${selectedTracks.size} bài hát")
            }
            finish()
        }

        binding.checkboxSelectAll.setOnClickListener {
            val isChecked = binding.checkboxSelectAll.isChecked
            addSongAdapter.toggleSelectAll(isChecked)
            updateToolbarTitle()
        }
    }

    private fun updateToolbarTitle() {
        val count = addSongAdapter.getSelectedCount()
        if (count == 0) {
            binding.tvAddSong.text = "Add songs"
        } else {
            binding.tvAddSong.text = "$count songs selected"
        }
    }

    private fun updateSelectAllCheckbox() {
        val totalItems = addSongAdapter.itemCount
        val selectedItems = addSongAdapter.getSelectedCount()

        binding.checkboxSelectAll.isChecked = (totalItems > 0 && totalItems == selectedItems)
    }
}