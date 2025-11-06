package com.example.baseproject.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.adapters.SongAdapter
import com.example.baseproject.bases.BaseActivity
import com.example.baseproject.databinding.ActivityPlaylistInfoBinding
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.example.baseproject.viewmodel.PlaylistInfoViewModel

class PlaylistInfoActivity :
    BaseActivity<ActivityPlaylistInfoBinding>(ActivityPlaylistInfoBinding::inflate) {

    private var playlistId: Long = -1L
    private val viewModel: PlaylistInfoViewModel by viewModels()
    private val sharedViewModel: MusicSharedViewModel by viewModels()
    private lateinit var songAdapter: SongAdapter

    override fun initData() {
        playlistId = intent.getLongExtra("PLAYLIST_ID", -1L)

        if (playlistId == -1L) {
            showToast("Lỗi: Không tìm thấy playlist.")
            finish()
            return
        }

        viewModel.loadPlaylist(playlistId)
    }

    override fun initView() {

        songAdapter = SongAdapter(
            onSongClick = { song ->
                sharedViewModel.selectSong(song)
            },
            onTymClicked = { song ->
            },
            onMoreClicked = { song ->
            }
        )

        binding.rvListSongs.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(this@PlaylistInfoActivity)
        }
    }

    override fun initActionView() {

        viewModel.playlistWithTracks.observe(this) { playlistData ->
            if (playlistData == null) return@observe

            val playlist = playlistData.playlist
            val tracks = playlistData.tracks

            binding.tvPlaylistName.text = playlist.name
            binding.tvPlaylistSongCount.text = "${tracks.size} songs - "

            Glide.with(this)
                .load(playlist.albumArtUri)
                .placeholder(R.drawable.fake_bg_2)
                .into(binding.ivPlaylistArt)

            songAdapter.submitList(tracks)
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}