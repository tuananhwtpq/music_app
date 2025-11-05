package com.example.baseproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.repository.PlaylistRepository
import kotlinx.coroutines.launch

class PLaylistsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var playlistRepo: PlaylistRepository

    companion object {
        const val TAG = "PlaylistsViewModel"
    }

    init {
        val db = SongsDatabase.getInstance(application)

        playlistRepo = PlaylistRepository(
            playlistDao = db.playlistDao(),
            context = application
        )
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            try {
                playlistRepo.getPlayListWithTracks()
            } catch (e: Exception) {
                Log.d(TAG, "Error when get all playlist: ${e.message}")
            }
        }
    }

    fun insertPLaylist(playlistName: String) {
        viewModelScope.launch {
            try {
                playlistRepo.insertPlaylist(playlistName)
            } catch (e: Exception) {
                Log.d(TAG, "Error when insert playlist: ${e.message}")
            }
        }
    }


}