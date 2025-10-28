package com.example.baseproject.fragments

import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.baseproject.databinding.FragmentPlayerBottomSheetDialogBinding
import com.example.baseproject.service.MyPlaybackService
import com.example.baseproject.viewmodel.MusicSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class PlayerBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "PlayerBottomSheetDialog_DEBUG"
        fun newInstance(): PlayerBottomSheetDialogFragment {
            return PlayerBottomSheetDialogFragment()
        }
    }

    private var _binding: FragmentPlayerBottomSheetDialogBinding? = null
    private val binding
        get() = _binding!!

    private var mediaController: MediaController? = null
    private lateinit var controllerFeature: ListenableFuture<MediaController>

    private val sharedViewModel: MusicSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        observedSharedViewModel()
    }

    private fun observedSharedViewModel() {
        sharedViewModel.isPlayerSheetVisible.observe(viewLifecycleOwner) { isVisible ->
            if (!isVisible) {
                dismiss()
            }
        }
        sharedViewModel.currentSongPlaying.observe(viewLifecycleOwner) { song ->
            binding.tvSongTitle.text = song?.title ?: "Unknown Song"
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        sharedViewModel.setPlayerSheetVisibility(false)
        sharedViewModel.setPlayerSheetVisibility(false)
        dialog.dismiss()
    }

    override fun dismiss() {
        super.dismiss()
        if (dialog?.isShowing == true) {
            dialog?.hide()
        }
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

        }, MoreExecutors.directExecutor())
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