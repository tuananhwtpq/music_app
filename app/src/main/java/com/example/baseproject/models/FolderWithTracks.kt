package com.example.baseproject.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FolderWithTracks(
    @Embedded
    val folder: Folders,

    @Relation(
        parentColumn = "folder_id",
        entityColumn = "mediaStoreId",
        associateBy = Junction(FolderTrackCrosRef::class)
    )
    val tracks: List<Track>
)
