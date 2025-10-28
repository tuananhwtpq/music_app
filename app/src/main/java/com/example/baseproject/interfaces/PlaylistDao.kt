package com.example.baseproject.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.baseproject.models.Playlist

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayList(playlist: Playlist)

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlayLists(): List<Playlist>

    @Query("SELECT * FROM playlists WHERE play_list_id = :playListId")
    suspend fun getPlayListById(playListId: Long): Playlist
}