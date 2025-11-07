package com.example.baseproject.activities

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.adapters.SongAdapter
import com.example.baseproject.bases.BaseActivity
import com.example.baseproject.databinding.ActivityFolderInfoBinding
import com.example.baseproject.fragments.TrackInfoFragment
import com.example.baseproject.models.Track
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.utils.ex.toDurationString
import com.example.baseproject.viewmodel.FolderInfoViewModel
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.example.baseproject.viewmodel.SongViewModel

class FolderInfoActivity :
    BaseActivity<ActivityFolderInfoBinding>(ActivityFolderInfoBinding::inflate) {

    private var folderId: Long = -1L
    private var folderName: String? = null

    private lateinit var songAdapter: SongAdapter
    private val viewModel: FolderInfoViewModel by viewModels()
    private val sharedViewModel: MusicSharedViewModel by viewModels()
    private val songViewModel: SongViewModel by viewModels()

    private var currentTracks: List<Track> = emptyList()


    override fun initData() {

        folderId = intent.getLongExtra("FOLDER_ID", -1L)
        folderName = intent.getStringExtra("FOLDER_NAME")

        if (folderId == -1L) {
            showToast("Lỗi: Không tìm thấy thư mục.")
            finish()
            return
        }

        viewModel.loadTracks(folderId)
    }

    override fun initView() {

        songAdapter = SongAdapter(
            onSongClick = { track ->
                sharedViewModel.selectSong(track)
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
            layoutManager = LinearLayoutManager(this@FolderInfoActivity)
        }
    }

    override fun initActionView() {

        viewModel.tracks.observe(this) { tracksData ->
            if (tracksData == null) return@observe

            currentTracks = tracksData

            binding.tvPlaylistName.text = folderName
            binding.tvPlaylistSongCount.text = "${tracksData.size} songs"

            var itemTime = 0L
            for (i in 0 until tracksData.size) {
                itemTime += tracksData[i].duration
            }
            val formattedTime = itemTime.toDurationString()
            binding.tvPlaylistDuration.text = "Length $formattedTime"

            songAdapter.submitList(tracksData)

        }

        sharedViewModel.favoriteStatusChange.observe(this) { statusChange ->
            if (statusChange == null) return@observe

            val (trackId, isFavorite) = statusChange

            val newList = songAdapter.tracks.toMutableList()

            val index = newList.indexOfFirst { it.mediaStoreId == trackId }

            if (index != -1) {
                val oldTrack = newList[index]
                val newTrack = oldTrack.copy(isFavorite = isFavorite)

                newList[index] = newTrack

                songAdapter.submitList(newList)
            }

            sharedViewModel.onFavoriteChangeHandled()
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
}