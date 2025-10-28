package com.example.baseproject.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class Folders(
    @PrimaryKey
    @ColumnInfo("folder_id") val folderId: Long,
    @ColumnInfo("folder_path") val folderPath: String
)
