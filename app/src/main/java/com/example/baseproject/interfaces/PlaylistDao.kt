package com.example.baseproject.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.baseproject.models.PlayListSongCrossRef
import com.example.baseproject.models.Playlist
import com.example.baseproject.models.PlaylistWithTracks
import com.example.baseproject.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayList(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(crossRef: PlayListSongCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTracksToPlaylist(crossRefs: List<PlayListSongCrossRef>)

    @Query("SELECT * FROM playlist_song_cross_ref WHERE play_list_id = :playlistId ORDER BY order_in_playlist ASC")
    suspend fun getQueueCrossRef(playlistId: Long): List<PlayListSongCrossRef>

    @Query("DELETE FROM playlist_song_cross_ref WHERE play_list_id = :playlistId AND media_store_id = :mediaStoreId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, mediaStoreId: Long)

    @Query("SELECT * FROM playlists WHERE play_list_id = :playListId")
    suspend fun getPlayListById(playListId: Long): Playlist

    @Transaction
    @Query("SELECT * FROM PLAYLISTS")
    fun getPlayListWithTracks(): Flow<List<PlaylistWithTracks>>

    @Query("SELECT * FROM tracks WHERE media_store_id IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<Track>

    @Transaction
    @Query("SELECT * FROM playlists WHERE play_list_id = :playlistId")
    fun getPlaylistWithTracksById(playlistId: Long): Flow<PlaylistWithTracks>

    @Transaction
    @Query("SELECT * FROM playlists WHERE play_list_id = :playlistId")
    suspend fun getPlaylistWithTracksByIdOnce(playlistId: Long): PlaylistWithTracks?

    @Query("DELETE FROM playlist_song_cross_ref WHERE play_list_id = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Transaction
    suspend fun updatePlaylistTracks(playlistId: Long, tracks: List<PlayListSongCrossRef>) {
        clearPlaylist(playlistId)
        insertAllTracksToPlaylist(tracks)
    }
}