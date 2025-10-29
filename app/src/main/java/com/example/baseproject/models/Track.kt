package com.example.baseproject.models

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(
    tableName = "tracks",
)
data class Track(
    @PrimaryKey
    @ColumnInfo("media_store_id") var mediaStoreId: Long,
    @ColumnInfo("title") var title: String,
    @ColumnInfo("artist") var artist: String,
    @ColumnInfo("album") var album: String? = null,
    @ColumnInfo("genre") var genre: String? = null,
    @ColumnInfo("duration") var duration: Long,
    @ColumnInfo("uri") val uri: Uri,
    @ColumnInfo("album_art_uri") var albumArtUri: Uri?,
    @ColumnInfo("is_favorite") var isFavorite: Boolean = false,
    @ColumnInfo("date_added") var dateAdded: Int? = null,
    @ColumnInfo("year") var year: Int? = null,
) : Parcelable, ListItem()


