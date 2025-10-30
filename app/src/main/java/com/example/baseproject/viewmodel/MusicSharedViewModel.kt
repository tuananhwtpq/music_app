package com.example.baseproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.session.MediaController
import com.example.baseproject.models.Track

class MusicSharedViewModel : ViewModel() {

    companion object {
        const val TAG = "MusicSharedViewModel"
    }

    private val _currentTrackPlaying = MutableLiveData<Track?>(null)
    val currentTrackPlaying: LiveData<Track?> = _currentTrackPlaying

    private val _isPlayerSheetVisible = MutableLiveData<Boolean>(false)
    val isPlayerSheetVisible: LiveData<Boolean> = _isPlayerSheetVisible

    private val _trackAddToQueue = MutableLiveData<Track?>(null)
    val trackAddToQueue: LiveData<Track?> = _trackAddToQueue

    private val _mediaController = MutableLiveData<MediaController?>(null)
    val mediaController: LiveData<MediaController?> = _mediaController

    private val _favoriteStatusChange = MutableLiveData<Pair<Long, Boolean>?>(null)
    val favoriteStatusChange: LiveData<Pair<Long, Boolean>?> = _favoriteStatusChange

    fun setTrackAddToQueue(track: Track) {
        _trackAddToQueue.value = track
        Log.d(TAG, "Track added to queue: ${_trackAddToQueue.value?.title}")
    }

    fun handleTrackAddedToQueue() {
        _trackAddToQueue.value = null
        Log.d(TAG, "Handle track added to queue successfully. trackAddToQueue reset to null")
    }

    fun setMediaController(controller: MediaController?) {
        _mediaController.value = controller
        Log.d(TAG, "Set mediacontroller successfully")
    }

    fun selectSong(track: Track) {
        _currentTrackPlaying.value = track
        Log.d(TAG, "Current track playing: ${_currentTrackPlaying.value?.title}")
    }

    fun setPlayerSheetVisibility(isVisible: Boolean) {
        _isPlayerSheetVisible.value = isVisible

        Log.d(TAG, "Is Player sheet visible: $isVisible")
    }

    fun onFavoriteChanged(trackId: Long, isFavorite: Boolean) {
        _favoriteStatusChange.value = Pair(trackId, isFavorite)
    }

    fun onFavoriteChangeHandled() {
        _favoriteStatusChange.value = null
    }

}