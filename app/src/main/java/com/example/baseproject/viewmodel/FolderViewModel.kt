package com.example.baseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.models.Folders
import com.example.baseproject.repository.PlaylistRepository
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {

    private val playlistRepository: PlaylistRepository

    private val _folders = MutableLiveData<List<Folders>>()
    val folders: LiveData<List<Folders>> = _folders

    init {
        val db = SongsDatabase.getInstance(application)
        playlistRepository = PlaylistRepository(db.playlistDao(), db.trackDao(), application)
    }

    fun loadFolders() {
        viewModelScope.launch {
            _folders.value = playlistRepository.getMusicFolders()
        }
    }
}