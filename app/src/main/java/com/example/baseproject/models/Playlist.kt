package com.example.baseproject.models

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("play_list_id") val playListId: Long,
    @ColumnInfo("name") var name: String,
    @ColumnInfo("album_art_uri") var albumArtUri: Uri? = null,
    @ColumnInfo("is_pin") var isPin: Boolean? = false,

    ) : Parcelable
