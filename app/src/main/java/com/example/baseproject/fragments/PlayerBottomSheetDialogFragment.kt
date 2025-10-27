package com.example.baseproject.fragments

import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.baseproject.databinding.FragmentPlayerBottomSheetDialogBinding
import com.example.baseproject.models.Song
import com.example.baseproject.service.MyPlaybackService
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class PlayerBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "PlayerBottomSheetDialogFragment"

        fun newInstance(): PlayerBottomSheetDialogFragment {
            return PlayerBottomSheetDialogFragment()
        }
    }

    private var _binding: FragmentPlayerBottomSheetDialogBinding? = null
    private val binding
        get() = _binding!!

    private var mediaController: MediaController? = null
    private lateinit var controllerFeature: ListenableFuture<MediaController>
    private var songToPlay: Song? = null

    private val sharedViewModel: MusicSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPLayer()
        initController()
        observedSharedViewModel()
    }

    private fun observedSharedViewModel() {
        //sharedViewModel.setPlayerSheetVisibility(true)
        sharedViewModel.isPlayerSheetVisible.observe(viewLifecycleOwner) { isVisible ->
            if (!isVisible) {
                dismiss()
            }
        }

        sharedViewModel.currentSongPlaying.observe(viewLifecycleOwner) { song ->
            songToPlay = song
            binding.tvSongTitle.text = song?.title ?: "Unknown Song"
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        sharedViewModel.setPlayerSheetVisibility(false)
    }

    private fun initPLayer() {

        val serviceIntent = Intent(requireContext(), MyPlaybackService::class.java)
        requireContext().startService(serviceIntent)
    }

    private fun initController() {
        val sessionToken = SessionToken(
            requireContext(), ComponentName(
                requireContext(),
                MyPlaybackService::class.java
            )
        )

        controllerFeature = MediaController.Builder(requireContext(), sessionToken).buildAsync()
        controllerFeature.addListener({
            mediaController = controllerFeature.get()

            binding.playerView.player = mediaController

            playNewSong()
        }, MoreExecutors.directExecutor())
    }

    private fun playNewSong() {
        songToPlay?.let { song ->
            val mediaItem = MediaItem.fromUri(song.uri)
            mediaController?.setMediaItem(mediaItem)
            mediaController?.prepare()
            mediaController?.play()
        }
    }

    override fun onStop() {
        super.onStop()
        MediaController.releaseFuture(controllerFeature)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
        _binding = null
    }

}