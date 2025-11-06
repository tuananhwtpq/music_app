package com.example.baseproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.models.Playlist
import com.example.baseproject.models.PlaylistWithTracks
import com.example.baseproject.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PLaylistsViewModel(application: Application) : AndroidViewModel(application) {

    private var playlistRepo: PlaylistRepository

    private val _smartPlaylists = MutableLiveData<List<PlaylistWithTracks>>()
    val smartPlaylists: LiveData<List<PlaylistWithTracks>> = _smartPlaylists
    val userPlaylists: LiveData<List<PlaylistWithTracks>>

    companion object {
        const val TAG = "PlaylistsViewModel"
        const val FAVORITE_ID = -1L
        const val RECENTLY_PLAYED_ID = -2L
        const val RECENTLY_ADDED_ID = -3L
        const val MOST_PLAYED_ID = -4L
    }

    init {
        val db = SongsDatabase.getInstance(application)
        playlistRepo = PlaylistRepository(db.playlistDao(), db.trackDao(), application)

        userPlaylists = playlistRepo.getAllPlaylistsWithTracks().asLiveData()

        loadSmartPlaylists()
    }

    fun loadSmartPlaylists() {
        viewModelScope.launch {
            val favTracksJob = async(Dispatchers.IO) { playlistRepo.getFavoriteTracks() }
            val recentTracksJob = async(Dispatchers.IO) { playlistRepo.getRecentlyAddedTracks() }
            val allTracksJob = async(Dispatchers.IO) { playlistRepo.getALlTracks() }

            val favTracks = favTracksJob.await()
            val recentTracks = recentTracksJob.await()
            val allTracks = allTracksJob.await()

            val smartList = mutableListOf<PlaylistWithTracks>()

            smartList.add(
                PlaylistWithTracks(
                    playlist = Playlist(playListId = FAVORITE_ID, name = "Bài hát yêu thích"),
                    tracks = favTracks
                )
            )

            smartList.add(
                PlaylistWithTracks(
                    playlist = Playlist(playListId = RECENTLY_PLAYED_ID, name = "Đã chơi gần đây"),
                    tracks = allTracks
                )
            )

            smartList.add(
                PlaylistWithTracks(
                    playlist = Playlist(playListId = RECENTLY_ADDED_ID, name = "Đã thêm gần đây"),
                    tracks = recentTracks
                )
            )

            smartList.add(
                PlaylistWithTracks(
                    playlist = Playlist(playListId = MOST_PLAYED_ID, name = "Nghe nhiều nhất"),
                    tracks = allTracks
                )
            )
            _smartPlaylists.postValue(smartList)
        }
    }


}