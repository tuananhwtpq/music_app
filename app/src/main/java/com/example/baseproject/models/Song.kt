package com.example.baseproject.models

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String? = null,
    val genre: String? = null,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri?
)
