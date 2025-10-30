package com.example.baseproject.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TrackWithPlaylists(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "play_list_id",
        associateBy = Junction(PlayListSongCrossRef::class)
    )
    val playlists: List<Playlist>
)
