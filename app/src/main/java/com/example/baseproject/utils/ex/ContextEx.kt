package com.example.baseproject.utils.ex

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.baseproject.models.Track
import com.example.baseproject.utils.Config
import com.example.baseproject.utils.Constants.PREFS_KEY

val Context.config: Config get() = Config.Companion.newInstance(applicationContext)

fun Context.getSharedPrefs() = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Track.toMediaItem(): MediaItem {
    val extras = Bundle().apply {
        putLong("mediaStoreId", this@toMediaItem.mediaStoreId)
    }

    val mediaItem = MediaMetadata.Builder()
        .setTitle(this.title)
        .setArtist(this.artist)
        .setAlbumTitle(this.album)
        .setExtras(extras)
        .setArtworkUri(this.albumArtUri)
        .build()

    return MediaItem.Builder()
        .setUri(this.uri)
        .setMediaId(this.mediaStoreId.toString())
        .setMediaMetadata(mediaItem)
        .build()
}

//fun Context.isTabVisible(flag: Int) = config.showTabs and flag != 0

//fun Context.getVisibleTabs() = Constants.tabList.filter { isTabVisible(it) }