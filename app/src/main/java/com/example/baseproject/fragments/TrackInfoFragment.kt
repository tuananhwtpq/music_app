package com.example.baseproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baseproject.databinding.FragmentTrackInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrackInfoFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "TrackInfoFragment_DEBUG"
        fun newInstance(): TrackInfoFragment {
            return TrackInfoFragment()
        }
    }

    private var _binding: FragmentTrackInfoBinding? = null
    val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTrackInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}