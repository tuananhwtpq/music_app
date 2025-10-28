package com.example.baseproject.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithTracks(
    @Embedded val playlist: Playlist,

    @Relation(
        parentColumn = "playListId",
        entityColumn = "mediaStoreId",
        associateBy = Junction(PlayListSongCrossRef::class)
    )
    val tracks: List<Track>
)
