package com.example.baseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.models.PlaylistWithTracks
import com.example.baseproject.repository.PlaylistRepository

class PlaylistInfoViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: PlaylistRepository

    init {
        val db = SongsDatabase.getInstance(application)
        repository = PlaylistRepository(db.playlistDao(), db.trackDao(), application)
    }
    private val _playlistId = MutableLiveData<Long>()

    val playlistWithTracks: LiveData<PlaylistWithTracks> = _playlistId.switchMap { id ->
        repository.getPlaylistWithTracksById(id).asLiveData()
    }

    fun loadPlaylist(id: Long) {
        _playlistId.value = id
    }

}