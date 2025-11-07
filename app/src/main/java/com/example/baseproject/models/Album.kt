package com.example.baseproject.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val albumId: Long,
    val albumName: String,
    val artist: String,
    val artUri: Uri?,
    val trackCount: Int

) : Parcelable