package com.example.baseproject.fragments

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentPlayerBottomSheetDialogBinding
import com.example.baseproject.models.Song
import com.example.baseproject.service.MyPlaybackService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class PlayerBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "PlayerBottomSheetDialogFragment"

        fun newInstance(song: Song): PlayerBottomSheetDialogFragment {
            val args = Bundle().apply {
                putParcelable("SONG_TO_PLAY", song)
            }

            return PlayerBottomSheetDialogFragment().apply {
                arguments = args
            }
        }
    }

    private var _binding: FragmentPlayerBottomSheetDialogBinding? = null
    private val binding
        get() = _binding!!

    private var mediaController: MediaController? = null
    private lateinit var controllerFeature: ListenableFuture<MediaController>
    private var songToPlay: Song? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            songToPlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable("SONG_TO_PLAY", Song::class.java)
            } else {
                it.getParcelable("SONG_TO_PLAY")
            }
        }
    }

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
        binding.tvSongTitle.text = songToPlay?.title ?: "Unknown Song"
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