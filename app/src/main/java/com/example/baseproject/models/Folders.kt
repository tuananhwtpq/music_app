package com.example.baseproject.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Folders(
    val folderId: Long,
    val folderName: String,
    val tracks: List<Track>
) : Parcelable {
    val trackCount: Int
        get() = tracks.size

    val firstTrackAlbumUri: Uri?
        get() = tracks.firstOrNull()?.albumArtUri
}
