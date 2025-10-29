package com.example.baseproject.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.baseproject.databinding.FragmentTrackInfoBinding
import com.example.baseproject.models.Track
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrackInfoFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "TrackInfoFragment_DEBUG"
        fun newInstance(track: Track): TrackInfoFragment {

            val args = Bundle()
            args.putParcelable("TRACK_INFO", track)

            return TrackInfoFragment().apply {
                arguments = args
            }
        }
    }

    private var _binding: FragmentTrackInfoBinding? = null
    val binding
        get() = _binding!!

    private val sharedViewModel: MusicSharedViewModel by activityViewModels()
    private var currentTrack: Track? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTrackInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTrack = arguments?.getParcelable("TRACK_INFO")
        Log.d(TAG, "onCreate: Current track info: ${currentTrack?.title}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTrack?.let {
            binding.tvSongName.text = it.title

            Glide.with(requireContext())
                .load(it.albumArtUri)
                .into(binding.tvSongImage)
        }

        binding.layoutAddToQueue.setOnClickListener {
            currentTrack.let { track ->
                if (track != null) {
                    sharedViewModel.setTrackAddToQueue(track)
                    Log.d(TAG, "onViewCreated: Added to queue: ${track.title}")
                }
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}