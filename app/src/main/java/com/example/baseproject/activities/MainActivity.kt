package com.example.baseproject.activities

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.adapters.MainViewPagerAdapter
import com.example.baseproject.bases.BaseActivity
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.databinding.ActivityMainBinding
import com.example.baseproject.fragments.PlayStackBottomSheetFragment
import com.example.baseproject.fragments.PlayerBottomSheetDialogFragment
import com.example.baseproject.models.PlayListSongCrossRef
import com.example.baseproject.models.Track
import com.example.baseproject.service.MyPlaybackService
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var toggle: ActionBarDrawerToggle
    private var mAdapter: MainViewPagerAdapter? = null
    private var mediaController: MediaController? = null
    private lateinit var controllerFeature: ListenableFuture<MediaController>
    private val sharedViewModel: MusicSharedViewModel by viewModels()
    private var isRestoringQueue = false

    private var queueSaveJob: Job? = null
    private val saveMutex = Mutex()

    //region PLAYER LISTENER
    private val playerListener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            updateMiniPlayerMetadata(mediaMetadata)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            updateMiniPlayerPlayPause(isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            val isSheetVisible = sharedViewModel.isPlayerSheetVisible.value ?: false

            updateMiniPlayerVisibility(playbackState, isSheetVisible = isSheetVisible)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Permission granted")
            } else {
                showPermissionRequest()
            }
        }

    override fun initData() {
        mAdapter = MainViewPagerAdapter(this)
    }

    //region INIT VIEW
    override fun initView() {
        binding.mainViewPager.adapter = mAdapter

        setupTabLayout()
        handleRequestPermission()
        setupDrawerLayout()
        setupItemToolBar()

        val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        }

        binding.mainViewPager.registerOnPageChangeCallback(onPageChangeCallback)

    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    //region INIT PLAYER
    private fun initPlayer() {

        val sessionToken = SessionToken(this, ComponentName(this, MyPlaybackService::class.java))

        controllerFeature = MediaController.Builder(this, sessionToken).buildAsync()

        controllerFeature.addListener(
            {
                mediaController = controllerFeature.get()

                sharedViewModel.setMediaController(mediaController)

                restoreQueueFromDatabase()

                mediaController?.addListener(playerListener)
                updateUiFromController(mediaController)

            }, MoreExecutors.directExecutor()
        )
    }

    //region SAVE QUEUE TO DATABASE
    private fun saveCurrentQueueToDatabase() {
        val controller = mediaController ?: return
        val currentQueueSize = controller.mediaItemCount

        Log.d(TAG, "Save current queue database")
        Log.d(TAG, "current QueueSize: $currentQueueSize")

        if (currentQueueSize == 0) {
            Log.d(TAG, "Queue is empty, clearing DB.")
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val db = SongsDatabase.getInstance(applicationContext)
                    db.playlistDao().clearPlaylist(MyPlaybackService.PLAY_STACK_ID)
                } catch (e: Exception) {
                    Log.e(TAG, "Error clearing empty queue", e)
                }
            }
            return
        }

        val currentMediaItems = mutableListOf<MediaItem>()
        for (i in 0 until currentQueueSize) {
            currentMediaItems.add(controller.getMediaItemAt(i))
        }

        Log.d(TAG, "Saving current queue with $currentQueueSize tracks to database.")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = SongsDatabase.getInstance(applicationContext)

                val newQueueCrossRefs = currentMediaItems.mapIndexedNotNull { index, mediaItem ->
                    val mediaStoreId = mediaItem.mediaMetadata.extras?.getLong("mediaStoreId")
                        ?: return@mapIndexedNotNull null

                    PlayListSongCrossRef(
                        playListId = MyPlaybackService.PLAY_STACK_ID,
                        mediaStoreId = mediaStoreId,
                        orderInPlaylist = index
                    )
                }

                if (newQueueCrossRefs.isNotEmpty()) {
                    db.playlistDao().updatePlaylistTracks(
                        MyPlaybackService.PLAY_STACK_ID,
                        newQueueCrossRefs
                    )
                    Log.d(TAG, "Successfully saved ${newQueueCrossRefs.size} tracks to queue")
                }

                val currentQueue =
                    db.playlistDao().getQueueCrossRef(MyPlaybackService.PLAY_STACK_ID)
                Log.d(TAG, "Current tracks in DB: ${currentQueue.size}")

            } catch (e: Exception) {
                Log.e(TAG, "Error saving queue", e)
            }
        }


    }

    //region RESTORE QUEUE FROM DATABASE
    private fun restoreQueueFromDatabase() {
        val db = SongsDatabase.getInstance(applicationContext)
        isRestoringQueue = true

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val crossRefs = db.playlistDao().getQueueCrossRef(MyPlaybackService.PLAY_STACK_ID)
                val totalTracks = db.trackDao().getAllTracksOnce().size
                val totalCrossRefs =
                    db.playlistDao().getQueueCrossRef(MyPlaybackService.PLAY_STACK_ID).size
                Log.d(TAG, "Found ${crossRefs.size} tracks in saved queue")

                val mediaItemsToAdd = if (crossRefs.isEmpty() || totalCrossRefs < totalTracks) {
                    Log.d(
                        TAG,
                        "Saved queue is empty or incomplete, loading all tracks from library"
                    )
                    val allTracks = db.trackDao().getAllTracksOnce()
                    Log.d(TAG, "Using ${allTracks.size} tracks from library")

                    val initialQueueCrossRefs = allTracks.mapIndexed { index, track ->
                        PlayListSongCrossRef(
                            playListId = MyPlaybackService.PLAY_STACK_ID,
                            mediaStoreId = track.mediaStoreId,
                            orderInPlaylist = index
                        )
                    }
                    db.playlistDao().clearPlaylist(MyPlaybackService.PLAY_STACK_ID)
                    db.playlistDao().insertAllTracksToPlaylist(initialQueueCrossRefs)

                    allTracks
                } else {
                    val trackIds = crossRefs.map { it.mediaStoreId }
                    val tracks = db.trackDao().getTracksByIds(trackIds)

                    crossRefs.mapNotNull { crossRef ->
                        tracks.find { it.mediaStoreId == crossRef.mediaStoreId }
                    }
                }

                val items = mediaItemsToAdd.map { track ->
                    MediaItem.Builder()
                        .setUri(track.uri)
                        .setMediaId(track.uri.toString())
                        .setMediaMetadata(buildMetadataFromSong(track))
                        .build()
                }

                withContext(Dispatchers.Main) {
                    mediaController?.setMediaItems(items)
                    mediaController?.prepare()
                    Log.d(TAG, "Restored ${items.size} items to queue")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error restoring queue", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isRestoringQueue = false
                    Log.d(TAG, "Queue restore completed")
                }
            }
        }
    }


    //region OBSERVED DATA
    private fun observedSharedViewModel() {

        //handle song selected
        sharedViewModel.currentTrackPlaying.observe(this) { selectedSong ->

            val selectedSongId = selectedSong?.uri.toString()
            val currentMediaId = mediaController?.currentMediaItem?.mediaId

            if (selectedSongId == currentMediaId) {
                if (mediaController?.isPlaying == true) {
                    mediaController?.pause()
                } else {
                    mediaController?.play()
                }
                return@observe
            }

            var foundIndex = -1

            for (i in 0 until (mediaController?.mediaItemCount ?: 0)) {
                val item = mediaController?.getMediaItemAt(i)
                if (item?.mediaId == selectedSongId) {
                    foundIndex = i
                    break
                }
            }
            if (foundIndex != -1) {
                mediaController?.seekTo(foundIndex, 0L)
                mediaController?.prepare()
                mediaController?.play()
            } else {
                val mediaItem = MediaItem.Builder()
                    .setUri(selectedSong?.uri)
                    .setMediaId(selectedSongId)
                    .setMediaMetadata(buildMetadataFromSong(selectedSong))
                    .build()

                mediaController?.setMediaItem(mediaItem)
                mediaController?.prepare()
                mediaController?.play()
            }
        }

        //handle player sheet dialog visibility
        sharedViewModel.isPlayerSheetVisible.observe(this) { isVisible ->

            val existingSheet = supportFragmentManager.findFragmentByTag(
                PlayerBottomSheetDialogFragment.TAG
            ) as? PlayerBottomSheetDialogFragment


            if (isVisible) {
                if (existingSheet == null || existingSheet.isDetached) {
                    PlayerBottomSheetDialogFragment.newInstance()
                        .show(supportFragmentManager, PlayerBottomSheetDialogFragment.TAG)
                } else {
                    if (existingSheet.dialog == null || !existingSheet.dialog!!.isShowing) {
                        existingSheet.show(
                            supportFragmentManager,
                            PlayerBottomSheetDialogFragment.TAG
                        )
                    }
                }

            } else {
                existingSheet?.dismissAllowingStateLoss()
            }
            updateMiniPlayerVisibility(
                mediaController?.playbackState ?: Player.STATE_IDLE,
                isVisible
            )

        }

        //handle add track to queue
        sharedViewModel.trackAddToQueue.observe(this) { track ->
            if (track == null) return@observe

            val mediaItem = MediaItem.Builder()
                .setUri(track.uri)
                .setMediaId(track.uri.toString())
                .setMediaMetadata(buildMetadataFromSong(track))
                .build()

            val currentQueueSize = mediaController?.mediaItemCount ?: 0
            mediaController?.addMediaItem(mediaItem)

            lifecycleScope.launch(Dispatchers.IO) {

                try {

                    val db = SongsDatabase.getInstance(applicationContext)

                    db.trackDao().insertTrack(track)
                    val insertedTrack = db.trackDao().getTracksByIds(listOf(track.mediaStoreId))
                    if (insertedTrack.isEmpty()) {
                        Log.d(TAG, "Error inserting track into DB")
                        return@launch
                    }


                    val queueItem = PlayListSongCrossRef(
                        playListId = MyPlaybackService.PLAY_STACK_ID,
                        mediaStoreId = track.mediaStoreId,
                        orderInPlaylist = currentQueueSize,
                    )

                    db.playlistDao().addTrackToPlaylist(queueItem)
                    Log.d(TAG, "Added ${track.title} to DB queue at index ${currentQueueSize - 1}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding track to queue $e.message")
                }

            }

            sharedViewModel.handleTrackAddedToQueue()
        }

        //handle favorite status change
        sharedViewModel.favoriteStatusChange.observe(this) { isFavorite ->
            if (isFavorite == null) return@observe

            try {
                val mediaItemId = isFavorite.first
                val favoriteStatus = isFavorite.second
                val controller = mediaController ?: return@observe

                Log.d(TAG, "Mediaitem id: $mediaItemId")

                for (i in 0 until controller.mediaItemCount) {
                    val mediaItem = controller.getMediaItemAt(i)
                    val itemExtras = mediaItem.mediaMetadata.extras
                    val mediaStoreId = itemExtras?.getLong("mediaStoreId", -1L)

                    if (mediaStoreId == mediaItemId) {
                        val newExtras = Bundle(itemExtras).apply {
                            putBoolean("is_favorite", favoriteStatus)
                        }

                        val newMetaData = mediaItem.mediaMetadata.buildUpon()
                            .setExtras(newExtras)
                            .build()

                        val newItem = mediaItem.buildUpon().setMediaMetadata(newMetaData).build()
                        controller.replaceMediaItem(i, newItem)

                        val changeMediaItem = controller.getMediaItemAt(i)
                        Log.d(
                            TAG,
                            "Change item info: ${changeMediaItem.mediaMetadata.extras?.getBoolean("is_favorite")}"
                        )
                        Log.d(TAG, "Change favorite successfully")
                    }
                }
                sharedViewModel.onFavoriteChangeHandled()
            } catch (e: Exception) {
                Log.d(TAG, "Error change favorite: ${e.message}")
            }

        }
    }

    //region MINI PLAYER CLICKED
    private fun handleMiniPlayerItemClicked() {
        binding.miniPlayer.playPauseBtn.setOnClickListener {
            if (mediaController?.isPlaying == true) {
                mediaController?.pause()
            } else {
                mediaController?.play()
            }
        }

        binding.miniPlayer.root.setOnClickListener {
            sharedViewModel.setPlayerSheetVisibility(true)
        }

        binding.miniPlayer.playStackBtn.setOnClickListener {
            val playStackSheet = PlayStackBottomSheetFragment.newInstance()
            playStackSheet.show(supportFragmentManager, PlayStackBottomSheetFragment.TAG)
        }

        binding.miniPlayer.nextSongBtn.setOnClickListener {
            Log.d(TAG, "Next song clicked")
            sharedViewModel.mediaController.value?.seekToNextMediaItem()

            val currentQueueSize = sharedViewModel.mediaController.value?.mediaItemCount ?: 0
            Log.d(TAG, "Current queue size: $currentQueueSize")
            Log.d(
                TAG,
                "Current media index: ${sharedViewModel.mediaController.value?.currentMediaItemIndex}"
            )
            if (currentQueueSize == sharedViewModel.mediaController.value?.currentMediaItemIndex?.plus(
                    1
                )
            ) {
                showToast("This is the last song in the queue")
            }
        }
    }

    private fun buildMetadataFromSong(track: Track?): MediaMetadata {
        val extras = Bundle().apply {
            if (track != null) {
                putLong("mediaStoreId", track.mediaStoreId)
                putBoolean("is_favorite", track.isFavorite)
            }
        }
        return MediaMetadata.Builder()
            .setTitle(track?.title)
            .setArtist(track?.artist)
            .setAlbumTitle(track?.album)
            .setArtworkUri(track?.albumArtUri)
            .setExtras(extras)
            .build()
    }

    //region UPDATE UI CONTROLLER
    private fun updateUiFromController(mediaController: MediaController?) {

        if (mediaController == null) return

        val isSheetVisible = sharedViewModel.isPlayerSheetVisible.value ?: false

        updateMiniPlayerMetadata(mediaController.mediaMetadata)
        updateMiniPlayerPlayPause(mediaController.isPlaying)
        updateMiniPlayerVisibility(mediaController.playbackState, isSheetVisible = isSheetVisible)

    }

    //region HANDLE REQUEST PERMISSION
    private fun handleRequestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            when {
                //permission granted
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Permission granted")
                }

                //the second time user denied permission
                shouldShowRequestPermissionRationale(permission) -> {
                    showPermissionRequest()
                }
                //the first time user access app
                else -> {
                    requestPermissionLauncher.launch(permission)
                }
            }

        } else {
            Log.d(TAG, "Not API 33 -> Permission auto granted")
        }
    }

    private fun showPermissionRequest() {
        AlertDialog.Builder(this)
            .setTitle("Notification permission")
            .setMessage("We need notification permission to show media controller on notification when you play music")
            .setPositiveButton("OK") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Later", null)
            .show()
    }

    //region SETUP TAB LAYOUT
    private fun setupTabLayout() {
        TabLayoutMediator(binding.mainTabsHolder, binding.mainViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Songs"
                    tab.setIcon(R.drawable.outline_ad_24)
                }

                1 -> {
                    tab.text = "Playlists"
                    tab.setIcon(R.drawable.outline_ad_24)
                }

                2 -> {
                    tab.text = "Placeholders"
                    tab.setIcon(R.drawable.outline_ad_24)
                }

                3 -> {
                    tab.text = "Library"
                    tab.setIcon(R.drawable.outline_ad_24)
                }

                4 -> {
                    tab.text = "Folders"
                    tab.setIcon(R.drawable.outline_ad_24)
                }
            }
        }.attach()
    }


    //region SETUP ITEM TOOL BAR
    private fun setupItemToolBar() {

        val btnMenu = binding.toolBar.btnMenu
        val btnSearch = binding.toolBar.btnSearch

        btnMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    //region SETUP DRAWER LAYOUT
    private fun setupDrawerLayout() {

        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, R.string.nav_open, R.string.nav_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setSupportActionBar(binding.toolBar.toolBarMain)

        val drawerWidth = 350
        val drawerHeight = 50
        val destiny = resources.displayMetrics.density
        val slideRangeX = (drawerWidth * destiny).toInt()
        val slideRangeY = (drawerHeight * destiny).toInt()

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val slideX = slideRangeX * slideOffset
                val slideY = slideRangeY * slideOffset
                binding.mainLayout.translationX = slideX
                binding.mainLayout.translationY = slideY
            }

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }


    //region MINI PLAYER VISIBILITY
    private fun updateMiniPlayerVisibility(playbackState: Int, isSheetVisible: Boolean) {

        if (isSheetVisible) {
            binding.miniPlayer.root.visibility = View.GONE
            return
        }

        val isVisible =
            (playbackState == Player.STATE_READY ||
                    playbackState == Player.STATE_BUFFERING ||
                    mediaController?.isPlaying == true)

        binding.miniPlayer.root.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OnDestroy")
    }

    //region MINI PLAYER PAUSE PLAY
    private fun updateMiniPlayerPlayPause(isPlaying: Boolean) {
        val iconRes = if (isPlaying) {
            R.drawable.pau_btn_2
        } else {
            R.drawable.play_btn_2
        }

        binding.miniPlayer.playPauseBtn.setImageResource(iconRes)

    }

    //region MINI PLAYER DATA
    private fun updateMiniPlayerMetadata(mediaMetadata: MediaMetadata) {
        binding.miniPlayer.tvSongName.text = mediaMetadata.title ?: "Unknown Track"

        Glide.with(this)
            .load(mediaMetadata.artworkUri)
            .placeholder(R.drawable.download)
            .into(binding.miniPlayer.ivCurrent)
    }


    //region INIT ACTION VIEW
    override fun initActionView() {
        observedSharedViewModel()
        handleMiniPlayerItemClicked()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Onstop: Saving current queue zie: ${mediaController?.mediaItemCount}")
        queueSaveJob?.cancel()
        saveCurrentQueueToDatabase()
        lifecycleScope.launch {
            delay(500)
            mediaController?.removeListener(playerListener)
            MediaController.releaseFuture(controllerFeature)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

}