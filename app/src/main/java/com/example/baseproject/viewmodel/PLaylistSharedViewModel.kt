package com.example.baseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.models.Track
import com.example.baseproject.repository.PlaylistRepository
import kotlinx.coroutines.launch

class PLaylistSharedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlaylistRepository

    private val _createdPlaylistId = MutableLiveData<Long?>()
    val createdPlaylistId: LiveData<Long?> = _createdPlaylistId

    private val _allTracks = MutableLiveData<List<Track>>()
    val allTracks: LiveData<List<Track>> = _allTracks

    init {
        val db = SongsDatabase.getInstance(application)
        repository = PlaylistRepository(db.playlistDao(), db.trackDao(), application)
    }

    fun createNewPlaylist(name: String) {
        viewModelScope.launch {
            val newId = repository.insertNewPLaylist(name)
            _createdPlaylistId.value = newId
        }
    }

    fun loadAllTracksForAdding() {
        viewModelScope.launch {
            _allTracks.value = repository.getALlTracks()
        }
    }

    fun addSongsToPlaylist(playlistId: Long, tracks: List<Track>) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, tracks)
        }
    }

    fun onPlaylistCreationHandled() {
        _createdPlaylistId.value = null
    }


}