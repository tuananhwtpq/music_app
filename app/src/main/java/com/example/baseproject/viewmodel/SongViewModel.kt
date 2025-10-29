package com.example.baseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.models.Track
import com.example.baseproject.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongViewModel(application: Application) : AndroidViewModel(application) {
    private var trackRepository: TrackRepository

    init {
        val db = SongsDatabase.getInstance(application)
        trackRepository =
            TrackRepository(
                db.trackDao(),
                db.playlistDao(),
                application.applicationContext
            )
    }

    val trackList: LiveData<List<Track>> = trackRepository.allTrack.asLiveData()

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            trackRepository.asyncDataFromMediaStore()
        }
    }

    fun setTrackToPlayStack(track: Track) {
        viewModelScope.launch {
            trackRepository.addTrackToPlayStack(track)
        }
    }
}