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
    suspend fun insertTrackToPlayStack(track: Track)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<Track>)

    @Query("SELECT * FROM tracks")
    fun getAll(): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE media_store_id IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<Track>

    @Query("SELECT * FROM tracks")
    suspend fun getAllTracksOnce(): List<Track>


}