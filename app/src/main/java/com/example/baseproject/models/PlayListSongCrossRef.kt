package com.example.baseproject.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_song_cross_ref")
data class PlayListSongCrossRef(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Long = 0L,
    @ColumnInfo("play_list_id") val playListId: Long,
    @ColumnInfo("media_store_id", index = true) val mediaStoreId: Long,
    @ColumnInfo("order_in_playlist") var orderInPlaylist: Int
)
