package com.example.baseproject.activities

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
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
import com.example.baseproject.fragments.TrackInfoFragment
import com.example.baseproject.models.Track
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.example.baseproject.viewmodel.PlaylistInfoViewModel
import com.example.baseproject.viewmodel.SongViewModel

class PlaylistInfoActivity :
    BaseActivity<ActivityPlaylistInfoBinding>(ActivityPlaylistInfoBinding::inflate) {

    private var playlistId: Long = -1L
    private val viewModel: PlaylistInfoViewModel by viewModels()
    private val sharedViewModel: MusicSharedViewModel by viewModels()
    private val songViewModel: SongViewModel by viewModels()
    private var currentTracks: List<Track> = emptyList()
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
                Log.d(TAG, "Item clicked")
            },
            onTymClicked = { song ->
                val newFavoriteStatus = !song.isFavorite
                songViewModel.updateFavoriteStatus(
                    song.mediaStoreId,
                    newFavoriteStatus,
                    sharedViewModel
                )
            },
            onMoreClicked = { song ->

                val trackInfoDialog = TrackInfoFragment.newInstance(song)
                trackInfoDialog.show(supportFragmentManager, TrackInfoFragment.TAG)

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

            currentTracks = tracks

            binding.tvPlaylistName.text = playlist.name
            binding.tvPlaylistSongCount.text = "${tracks.size} songs - "

            Glide.with(this)
                .load(playlist.albumArtUri)
                .placeholder(R.drawable.fake_bg_2)
                .into(binding.ivPlaylistArt)

            songAdapter.submitList(tracks)
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.layoutPlayBtn.setOnClickListener {
            handlePlayButton()
        }

        binding.layoutShuffleBtn.setOnClickListener {
            handleShuffleButton()
        }
    }

    private fun handlePlayButton() {
        if (currentTracks.isEmpty()) {
            showToast("Playlist trống")
            return
        }
        val firstTrack = currentTracks[0]
        sharedViewModel.selectSong(firstTrack)
    }

    private fun handleShuffleButton() {
        if (currentTracks.isEmpty()) {
            showToast("Playlist trống")
            return
        }

        val firstTrack = currentTracks.shuffled()[0]
        sharedViewModel.selectSong(firstTrack)
    }

    companion object {
        const val TAG = "PlaylistInfoActivity"
    }
}