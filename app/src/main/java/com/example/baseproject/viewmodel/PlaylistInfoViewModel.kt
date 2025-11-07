package com.example.baseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.models.Playlist
import com.example.baseproject.models.PlaylistWithTracks
import com.example.baseproject.repository.PlaylistRepository
import com.example.baseproject.viewmodel.PLaylistsViewModel.Companion.FAVORITE_ID
import com.example.baseproject.viewmodel.PLaylistsViewModel.Companion.MOST_PLAYED_ID
import com.example.baseproject.viewmodel.PLaylistsViewModel.Companion.RECENTLY_ADDED_ID
import com.example.baseproject.viewmodel.PLaylistsViewModel.Companion.RECENTLY_PLAYED_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlaylistRepository

    init {
        val db = SongsDatabase.getInstance(application)
        repository = PlaylistRepository(db.playlistDao(), db.trackDao(), application)
    }

    private val _playlistId = MutableLiveData<Long>()
    private val _playlistWithTracks = MutableLiveData<PlaylistWithTracks?>()
    val playlistWithTracks: LiveData<PlaylistWithTracks?> = _playlistWithTracks

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist: PlaylistWithTracks? = repository.getPlaylistWithTracksById(playlistId)
            _playlistWithTracks.postValue(playlist)
        }
    }

    fun loadFavoritePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val favTracks = repository.getFavoriteTracks()
            val playlistData = PlaylistWithTracks(
                playlist = Playlist(playListId = FAVORITE_ID, name = "Bài hát yêu thích"),
                tracks = favTracks
            )
            _playlistWithTracks.postValue(playlistData)
        }
    }

    fun loadRecentlyAddedPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val recentTracks = repository.getRecentlyAddedTracks()
            val playlistData = PlaylistWithTracks(
                playlist = Playlist(playListId = RECENTLY_ADDED_ID, name = "Đã thêm gần đây"),
                tracks = recentTracks
            )
            _playlistWithTracks.postValue(playlistData)
        }
    }

    fun loadRecentlyPlayedPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val allTracks = repository.getALlTracks()
            val playlistData = PlaylistWithTracks(
                playlist = Playlist(playListId = RECENTLY_PLAYED_ID, name = "Đã chơi gần đây"),
                tracks = allTracks
            )
            _playlistWithTracks.postValue(playlistData)
        }
    }

    fun loadMostPlayedPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val allTracks = repository.getALlTracks()
            val playlistData = PlaylistWithTracks(
                playlist = Playlist(playListId = MOST_PLAYED_ID, name = "Nghe nhiều nhất"),
                tracks = allTracks
            )
            _playlistWithTracks.postValue(playlistData)
        }
    }

}