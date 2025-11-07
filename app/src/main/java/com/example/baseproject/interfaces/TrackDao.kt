package com.example.baseproject.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.baseproject.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<Track>)

    @Query("SELECT * FROM tracks")
    fun getAll(): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE media_store_id IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<Track>

    @Query("SELECT * FROM tracks")
    suspend fun getAllTracksOnce(): List<Track>

    @Query("SELECT * FROM tracks WHERE is_favorite = 1")
    suspend fun getFavoriteTracks(): List<Track>

    @Query("UPDATE tracks SET is_favorite = :isFavorite WHERE media_store_id = :trackId")
    suspend fun updateFavoriteStatus(trackId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM tracks ORDER BY date_added DESC")
    suspend fun getRecentlyAddedTracks(): List<Track>

}