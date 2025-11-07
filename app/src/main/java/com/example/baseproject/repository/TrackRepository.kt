package com.example.baseproject.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.baseproject.interfaces.PlaylistDao
import com.example.baseproject.interfaces.TrackDao
import com.example.baseproject.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TrackRepository(
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao,
    private val context: Context
) {

    val allTrack: Flow<List<Track>> = trackDao.getAll()

    suspend fun asyncDataFromMediaStore() {
        withContext(Dispatchers.IO) {

            val existingTrackMap = trackDao.getAllTracksOnce()
                .associateBy { it.mediaStoreId }

            val trackList = mutableListOf<Track>()

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.BUCKET_ID,
                MediaStore.Audio.Media.BUCKET_DISPLAY_NAME,
            )

            val selection =
                "${MediaStore.Audio.Media.IS_MUSIC} != 0"

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val dateAddedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_ID)
                val bucketName =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)


                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    val duration = cursor.getLong(durationColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    val bucketId = cursor.getLong(bucketIdColumn)
                    val bucketName = cursor.getString(bucketName)


                    val existingTrack = existingTrackMap[id]
                    val isFavorite = existingTrack?.isFavorite ?: false

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                    )

                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"), albumId
                    )

                    trackList.add(
                        Track(
                            mediaStoreId = id,
                            title = title,
                            artist = artist,
                            duration = duration,
                            uri = contentUri,
                            albumArtUri = albumArtUri,
                            album = null,
                            genre = null,
                            isFavorite = isFavorite,
                            dateAdded = dateAdded,
                            year = null,
                            bucketId = bucketId,
                            bucketDisplayName = bucketName
                        )
                    )
                }
            }
            trackDao.insertAll(trackList)
            Log.d("TrackRepository", "Track list: $trackList")
        }
    }

    suspend fun addTrackToPlayStack(track: Track) {
        withContext(Dispatchers.IO) {
            trackDao.insertTrack(track)
        }
    }

    suspend fun updateFavoriteStatus(trackId: Long, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            trackDao.updateFavoriteStatus(trackId, isFavorite)
        }
    }

    suspend fun getFavoriteTracks(): List<Track> {
        return withContext(Dispatchers.IO) {
            trackDao.getFavoriteTracks()
        }
    }

    suspend fun getRecentlyAddedTracks(): List<Track> {
        return withContext(Dispatchers.IO) {
            trackDao.getRecentlyAddedTracks()
        }
    }

}