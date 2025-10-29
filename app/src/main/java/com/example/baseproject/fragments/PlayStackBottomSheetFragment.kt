package com.example.baseproject.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.adapters.PlayStackAdapter
import com.example.baseproject.databinding.FragmentPlayStackBottomSheetBinding
import com.example.baseproject.viewmodel.MusicSharedViewModel
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

    private lateinit var playStackAdapter: PlayStackAdapter
    private val sharedViewModel: MusicSharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayStackBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupViewModel()
    }

    private fun setupViewModel() {
        sharedViewModel.mediaController.observe(viewLifecycleOwner) { controller ->
            if (controller == null) return@observe

            val queue = mutableListOf<MediaItem>()

            for (i in 0 until controller.mediaItemCount) {
                val item = controller.getMediaItemAt(i)
                queue.add(item)
            }

            playStackAdapter.submitData(queue)

            controller.addListener(object : Player.Listener {
                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    super.onTimelineChanged(timeline, reason)
                    val updatedQueue = mutableListOf<MediaItem>()

                    for (i in 0 until controller.mediaItemCount) {
                        val item = controller.getMediaItemAt(i)
                        updatedQueue.add(item)
                    }

                    playStackAdapter.submitData(updatedQueue)
                }
            })
        }
    }

    private fun setupAdapter() {
        playStackAdapter = PlayStackAdapter(
            onTymClicked = { track ->

            },

            onDeleteClicked = { mediaItem ->

                val indexItem = (0 until (sharedViewModel.mediaController.value?.mediaItemCount
                    ?: 0)).firstOrNull() { index ->
                    sharedViewModel.mediaController.value?.getMediaItemAt(index) == mediaItem
                }

                indexItem?.let {
                    sharedViewModel.mediaController.value?.removeMediaItem(it)
                }
                Log.d(
                    TAG,
                    "onDeleteClicked: Remove item at index $indexItem - Item title: ${mediaItem.mediaMetadata.title}"
                )
            },
            onItemClicked = { mediaItem ->

                val mediaItemIndex = (0 until (sharedViewModel.mediaController.value?.mediaItemCount
                    ?: 0)).firstOrNull { index ->
                    sharedViewModel.mediaController.value?.getMediaItemAt(index) == mediaItem
                } ?: 0

                sharedViewModel.mediaController.value?.seekTo(mediaItemIndex, 0L)
                Log.d(
                    TAG,
                    "onItemClicked: Seek to index $mediaItemIndex - Item title: ${mediaItem.mediaMetadata.title}"
                )
            }
        )

        binding.rvPlayStack.apply {
            adapter = playStackAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}