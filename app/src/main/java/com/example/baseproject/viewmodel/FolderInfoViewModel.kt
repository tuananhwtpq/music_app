package com.example.baseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.models.Track
import com.example.baseproject.repository.PlaylistRepository
import kotlinx.coroutines.launch

class FolderInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val playlistRepo: PlaylistRepository
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    init {
        val db = SongsDatabase.getInstance(application)
        playlistRepo = PlaylistRepository(db.playlistDao(), db.trackDao(), application)
    }

    fun loadTracks(folderId: Long) {
        viewModelScope.launch {
            val trackLists = playlistRepo.getTracksByFolderId(folderId)
            _tracks.value = trackLists
        }
    }


}