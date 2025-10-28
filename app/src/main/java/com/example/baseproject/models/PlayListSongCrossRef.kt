package com.example.baseproject.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["play_list_id", "media_store_id"], tableName = "playlist_song_cross_ref")
data class PlayListSongCrossRef(
    @ColumnInfo("play_list_id") val playListId: Long,
    @ColumnInfo("media_store_id", index = true) val mediaStoreId: Long,
    @ColumnInfo("order_in_playlist") var orderInPlaylist: Int
)
