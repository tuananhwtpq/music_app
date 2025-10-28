package com.example.baseproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baseproject.databinding.FragmentPlayStackBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlayStackBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "PlayStackBottomSheetFragment"

        fun newInstance(): PlayStackBottomSheetFragment {
            return PlayStackBottomSheetFragment()
        }
    }

    private var _binding: FragmentPlayStackBottomSheetBinding? = null
    val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayStackBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}