package com.example.baseproject.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["folder_id", "media_store_id"], tableName = "folder_track_cross_ref")
data class FolderTrackCrosRef(
    @ColumnInfo("folder_id") val folderId: Long,
    @ColumnInfo("media_store_id", index = true) val mediaStoreId: Long
)
