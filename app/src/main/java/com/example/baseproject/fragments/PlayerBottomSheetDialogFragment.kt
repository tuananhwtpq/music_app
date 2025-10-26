package com.example.baseproject.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentPlayerBottomSheetDialogBinding
import com.example.baseproject.models.Song
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

    private var exoPlayer: ExoPlayer? = null
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
        binding.tvSongTitle.text = songToPlay?.title ?: "Unknown Song"
    }

    private fun initPLayer() {
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = exoPlayer

        songToPlay?.let { song ->
            val mediaItem = MediaItem.fromUri(song.uri)

            exoPlayer?.setMediaItem(mediaItem)

            exoPlayer?.prepare()
            exoPlayer?.play()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            player.stop()
            player.release()
        }

        exoPlayer = null
        binding.playerView.player = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}