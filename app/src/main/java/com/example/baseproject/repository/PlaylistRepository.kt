package com.example.baseproject.repository

import android.content.Context
import com.example.baseproject.interfaces.PlaylistDao
import com.example.baseproject.interfaces.TrackDao
import com.example.baseproject.models.PlayListSongCrossRef
import com.example.baseproject.models.Playlist
import com.example.baseproject.models.PlaylistWithTracks
import com.example.baseproject.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val context: Context
) {

    suspend fun addTrackToPlayList(crossRef: PlayListSongCrossRef) {
        withContext(Dispatchers.IO) {
            playlistDao.addTrackToPlaylist(crossRef)
        }
    }

    suspend fun insertNewPLaylist(playlistName: String): Long {
        return withContext(Dispatchers.IO) {
            val newPlaylist = Playlist(name = playlistName)
            playlistDao.insertPlayList(newPlaylist)
        }
    }

    suspend fun getALlTracks(): List<Track> {
        return withContext(Dispatchers.IO) {
            trackDao.getAllTracksOnce()
        }
    }

    suspend fun addTrackToPlaylist(playlistId: Long, tracks: List<Track>) {
        withContext(Dispatchers.IO) {
            val crossRef = tracks.mapIndexed { index, track ->
                PlayListSongCrossRef(
                    playListId = playlistId, orderInPlaylist = index,
                    mediaStoreId = track.mediaStoreId,
                )
            }
            playlistDao.insertAllTracksToPlaylist(crossRef)
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

    fun getAllPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>> {
        return playlistDao.getPlayListWithTracks()
    }

    fun getPlaylistWithTracksById(playlistId: Long): Flow<PlaylistWithTracks> {
        return playlistDao.getPlaylistWithTracksById(playlistId)
    }

    suspend fun getFavoriteTracks(): List<Track> {
        return withContext(Dispatchers.IO) {
            trackDao.getFavoriteTracks()
        }
    }

    suspend fun getRecentlyAddedTracks(): List<Track> {
        return withContext(Dispatchers.IO) {
            trackDao.getRecentlyAddedTracks()
        }
    }
}