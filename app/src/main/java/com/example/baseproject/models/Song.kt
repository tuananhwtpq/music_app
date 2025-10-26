package com.example.baseproject.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String? = null,
    val genre: String? = null,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri?
) : Parcelable
