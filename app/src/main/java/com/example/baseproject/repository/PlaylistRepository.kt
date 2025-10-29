package com.example.baseproject.repository

import android.content.Context
import com.example.baseproject.interfaces.PlaylistDao
import com.example.baseproject.models.PlayListSongCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepository(private val playlistDao: PlaylistDao, private val context: Context) {

//    suspend fun getAllPlayLists() {
//        withContext(Dispatchers.IO) {
//            playlistDao.getAllPlayLists()
//        }
//    }

    suspend fun addTrackToPlayList(crossRef: PlayListSongCrossRef) {
        withContext(Dispatchers.IO) {
            playlistDao.addTrackToPlaylist(crossRef)
        }
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, mediaStoreId: Long) {
        withContext(Dispatchers.IO) {
            playlistDao.removeTrackFromPlaylist(playlistId, mediaStoreId)
        }
    }

    suspend fun getPlayListById(playListId: Long) {
        withContext(Dispatchers.IO) {
            playlistDao.getPlayListById(playListId)
        }
    }

    suspend fun getPlayListWithTracks() {
        withContext(Dispatchers.IO) {
            playlistDao.getPlayListWithTracks()
        }
    }

    suspend fun getPlaylistWithTracksById(playlistId: Long) {
        withContext(Dispatchers.IO) {
            playlistDao.getPlaylistWithTracksById(playlistId)
        }
    }
}