package com.example.baseproject.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("play_list_id") val playListId: Long,
    @ColumnInfo("name") var name: String

)
