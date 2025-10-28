package com.example.baseproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.baseproject.models.Track

class MusicSharedViewModel : ViewModel() {

    companion object {
        const val TAG = "MusicSharedViewModel"
    }

    private val _currentTrackPlaying = MutableLiveData<Track?>(null)
    val currentTrackPlaying: LiveData<Track?> = _currentTrackPlaying

    private val _isPlayerSheetVisible = MutableLiveData<Boolean>(false)
    val isPlayerSheetVisible: LiveData<Boolean> = _isPlayerSheetVisible

    fun selectSong(track: Track) {
        _currentTrackPlaying.postValue(track)
        Log.d(TAG, "Current track playing: ${_currentTrackPlaying.value?.title}")
    }

    fun setPlayerSheetVisibility(isVisible: Boolean) {
        _isPlayerSheetVisible.postValue(isVisible)
        Log.d(TAG, "Is Player sheet visible: $isVisible")
    }
}