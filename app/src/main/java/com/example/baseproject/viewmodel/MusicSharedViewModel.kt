package com.example.baseproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.baseproject.models.Song

class MusicSharedViewModel : ViewModel() {

    companion object {
        const val TAG = "MusicSharedViewModel"
    }

    private val _currentSongPlaying = MutableLiveData<Song?>(null)
    val currentSongPlaying: LiveData<Song?> = _currentSongPlaying

    private val _isPlayerSheetVisible = MutableLiveData<Boolean>(false)
    val isPlayerSheetVisible: LiveData<Boolean> = _isPlayerSheetVisible

    fun selectSong(song: Song) {
        _currentSongPlaying.postValue(song)
        Log.d(TAG, "Current song playing: ${_currentSongPlaying.toString()}")
    }

    fun setPlayerSheetVisibility(isVisible: Boolean) {
        _isPlayerSheetVisible.postValue(isVisible)
        Log.d(TAG, "Is Player sheet visible: $_isPlayerSheetVisible")
    }
}