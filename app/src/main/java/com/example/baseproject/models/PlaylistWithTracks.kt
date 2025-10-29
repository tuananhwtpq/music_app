package com.example.baseproject.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithTracks(
    @Embedded val playlist: Playlist,

    @Relation(
        parentColumn = "play_list_id",
        entityColumn = "media_store_id",
        associateBy = Junction(PlayListSongCrossRef::class)
    )
    val tracks: List<Track>
)
