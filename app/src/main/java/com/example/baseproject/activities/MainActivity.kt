package com.example.baseproject.activities

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import com.example.baseproject.databinding.ActivityMainBinding
import com.example.baseproject.fragments.PlayStackBottomSheetFragment
import com.example.baseproject.fragments.PlayerBottomSheetDialogFragment
import com.example.baseproject.models.Track
import com.example.baseproject.service.MyPlaybackService
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var toggle: ActionBarDrawerToggle
    private var mAdapter: MainViewPagerAdapter? = null

    private var mediaController: MediaController? = null
    private lateinit var controllerFeature: ListenableFuture<MediaController>

    private val sharedViewModel: MusicSharedViewModel by viewModels()

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

        //startingService()
        initPlayer()
    }

    private fun startingService() {
        val serviceIntent = Intent(this, MyPlaybackService::class.java)
        startService(serviceIntent)
    }

    //region INIT PLAYER
    private fun initPlayer() {

        val sessionToken = SessionToken(this, ComponentName(this, MyPlaybackService::class.java))

        controllerFeature = MediaController.Builder(this, sessionToken).buildAsync()

        controllerFeature.addListener(
            {
                mediaController = controllerFeature.get()

                sharedViewModel.setMediaController(mediaController)

                mediaController?.addListener(playerListener)
                updateUiFromController(mediaController)

            }, MoreExecutors.directExecutor()
        )
    }

    //region OBSERVED DATA
    private fun observedSharedViewModel() {

        sharedViewModel.currentTrackPlaying.observe(this) { selectedSong ->
            if (selectedSong == null) return@observe


            val currentMediaId = mediaController?.currentMediaItem?.mediaId
            Log.d(TAG, "Current media id: ${currentMediaId.toString()}")

            val newSongUriString = selectedSong.uri.toString()
            Log.d(TAG, "New song Uri String: $newSongUriString")


            if (newSongUriString != currentMediaId) {
                Log.d(TAG, "new song selected")

                val mediaItem = MediaItem.Builder()
                    .setUri(selectedSong.uri)
                    .setMediaId(newSongUriString)
                    .setMediaMetadata(buildMetadataFromSong(selectedSong))
                    .build()

                mediaController?.setMediaItem(mediaItem)
                mediaController?.prepare()
                mediaController?.play()

            } else {
                Log.d(TAG, "This is a Track")
            }
        }

        sharedViewModel.isPlayerSheetVisible.observe(this) { isVisible ->

            val existingSheet = supportFragmentManager.findFragmentByTag(
                PlayerBottomSheetDialogFragment.TAG
            ) as? PlayerBottomSheetDialogFragment


            if (isVisible) {
                if (existingSheet == null || existingSheet.isDetached) {
                    Log.d(TAG, "Show new sheet")
                    PlayerBottomSheetDialogFragment.newInstance()
                        .show(supportFragmentManager, PlayerBottomSheetDialogFragment.TAG)
                } else {
                    if (existingSheet.dialog == null || !existingSheet.dialog!!.isShowing) {
                        Log.d(TAG, "Show existing sheet")
                        existingSheet.show(
                            supportFragmentManager,
                            PlayerBottomSheetDialogFragment.TAG
                        )
                    }
                }

            } else {
                existingSheet?.dismissAllowingStateLoss()
                Log.d(TAG, "Dismiss sheet")
            }

            updateMiniPlayerVisibility(
                mediaController?.playbackState ?: Player.STATE_IDLE,
                isVisible
            )

        }

        sharedViewModel.trackAddToQueue.observe(this) { track ->
            if (track == null) return@observe

            val mediaItem = MediaItem.Builder()
                .setUri(track.uri)
                .setMediaId(track.uri.toString())
                .setMediaMetadata(buildMetadataFromSong(track))
                .build()

            mediaController?.addMediaItem(mediaItem)
            Log.d(TAG, "Track added to queue: ${track.title}")
            Log.d(TAG, "Total items in queue: ${mediaController?.mediaItemCount}")
            sharedViewModel.handleTrackAddedToQueue()
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
            //val currentIndex = sharedViewModel.mediaController.value.
        }
    }

    private fun buildMetadataFromSong(track: Track): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbumTitle(track.album)
            .setArtworkUri(track.albumArtUri)
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
        mediaController?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFeature)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

}