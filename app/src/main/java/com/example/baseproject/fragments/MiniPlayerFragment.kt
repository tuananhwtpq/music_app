package com.example.baseproject.fragments

import android.content.ComponentName
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentMiniPlayerBinding
import com.example.baseproject.service.MyPlaybackService
import com.example.baseproject.utils.ex.showToast
import com.example.baseproject.utils.ex.toMediaItem
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class MiniPlayerFragment :
    BaseFragment<FragmentMiniPlayerBinding>(FragmentMiniPlayerBinding::inflate) {

    companion object {
        const val TAG = "MiniPlayerFragment"
    }

    private val sharedViewModel: MusicSharedViewModel by activityViewModels()
    private var mediaController: MediaController? = null
    private lateinit var controllerFeature: ListenableFuture<MediaController>


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
            updateMiniPlayerVisibility(playbackState)
        }
    }

    override fun initData() {
        initController()
    }

    override fun initView() {

    }

    override fun initActionView() {
//        sharedViewModel.mediaController.observe(viewLifecycleOwner) { controller ->
//            if (controller != null) {
//                updateUiFromController(controller)
//                controller.addListener(playerListener)
//            }
//        }

        sharedViewModel.isPlayerSheetVisible.observe(viewLifecycleOwner) { isVisible ->
            updateMiniPlayerVisibility(
                sharedViewModel.mediaController.value?.playbackState ?: Player.STATE_IDLE,
                isVisible
            )
        }
        observeSongSelection()

        handleMiniPlayerItemClicked()
    }

    private fun observeSongSelection() {
        sharedViewModel.currentTrackPlaying.observe(viewLifecycleOwner) { selectedSong ->
            if (selectedSong == null || mediaController == null) return@observe

            val selectedSongUriString = selectedSong.uri.toString()
            val currentPlayingUriString = mediaController?.currentMediaItem?.mediaId

            if (selectedSongUriString == currentPlayingUriString) {
                if (mediaController?.isPlaying == true) {
                    mediaController?.pause()
                } else {
                    mediaController?.play()
                }
                Log.d(TAG, "Toggled play/pause for the current song.")
                return@observe
            }

            var foundIndex = -1
            for (i in 0 until (mediaController?.mediaItemCount ?: 0)) {
                if (mediaController?.getMediaItemAt(i)?.mediaId == selectedSongUriString) {
                    foundIndex = i
                    break
                }
            }

            if (foundIndex != -1) {
                Log.d(TAG, "Found selected song at index $foundIndex. Seeking and playing.")
                mediaController?.seekTo(foundIndex, 0L)
                mediaController?.prepare()
                mediaController?.play()
            } else {
                Log.w(TAG, "Selected song not found in queue. Playing as single item.")

                val mediaItem = selectedSong.toMediaItem()

                mediaController?.setMediaItem(mediaItem)
                mediaController?.prepare()
                mediaController?.play()
            }
        }
    }

    private fun initController() {
        val sessionToken = SessionToken(
            requireContext(), ComponentName(
                requireContext(),
                MyPlaybackService::class.java
            )
        )
        controllerFeature = MediaController.Builder(requireContext(), sessionToken).buildAsync()
        controllerFeature.addListener(
            {
                try {
                    mediaController = controllerFeature.get()
                    mediaController?.addListener(playerListener)
                    updateUiFromController(mediaController!!)
                    sharedViewModel.setMediaController(mediaController)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to get MediaController", e)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun handleMiniPlayerItemClicked() {
        //val controller = sharedViewModel.mediaController.value ?: return

        binding.playPauseBtn.setOnClickListener {
            mediaController?.let { controller ->
                if (controller.isPlaying) {
                    controller.pause()
                } else {
                    controller.play()
                }
            }
        }

        binding.root.setOnClickListener {
            sharedViewModel.setPlayerSheetVisibility(true)
        }

        binding.playStackBtn.setOnClickListener {
            mediaController?.let { controller ->
                val playStackSheet = PlayStackBottomSheetFragment.newInstance()
                playStackSheet.show(parentFragmentManager, PlayStackBottomSheetFragment.TAG)
            }
        }

        binding.nextSongBtn.setOnClickListener {

            mediaController?.let { controller ->
                controller.seekToNextMediaItem()

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

    }

    private fun updateUiFromController(mediaController: MediaController) {
        updateMiniPlayerMetadata(mediaController.mediaMetadata)
        updateMiniPlayerPlayPause(mediaController.isPlaying)
        updateMiniPlayerVisibility(mediaController.playbackState)
    }

    private fun updateMiniPlayerMetadata(mediaMetadata: MediaMetadata?) {
        if (mediaMetadata == null) return
        binding.tvSongName.text = mediaMetadata.title ?: "Unknown Track"
        Glide.with(this)
            .load(mediaMetadata.artworkUri)
            .placeholder(R.drawable.download)
            .into(binding.ivCurrent)
    }

    private fun updateMiniPlayerPlayPause(isPlaying: Boolean) {
        val iconRes = if (isPlaying) R.drawable.pau_btn_2 else R.drawable.play_btn_2
        binding.playPauseBtn.setImageResource(iconRes)
    }

    private fun updateMiniPlayerVisibility(playbackState: Int, isSheetVisible: Boolean = false) {
        if (isSheetVisible) {
            binding.root.visibility = View.GONE
            return
        }
        val isVisible = (playbackState == Player.STATE_READY ||
                playbackState == Player.STATE_BUFFERING ||
                this.mediaController?.isPlaying == true)
        binding.root.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStop() {
        super.onStop()
        mediaController?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFeature)
    }

}