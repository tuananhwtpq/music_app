package com.example.baseproject.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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

            val trackList = mutableListOf<Track>()

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

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

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    val duration = cursor.getLong(durationColumn)
                    val albumId = cursor.getLong(albumIdColumn)

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
                            isFavorite = false,
                            dateAdded = null,
                            year = null,
                        )
                    )
                }
            }
            trackDao.insertAll(trackList)

//            val currentQueue = playlistDao.getQueueCrossRef(MyPlaybackService.PLAY_STACK_ID)
//
//            if (currentQueue.isNotEmpty() && trackList.isNotEmpty()) {
//                val updateQueue = trackList.mapIndexed { index, track ->
//                    PlayListSongCrossRef(
//                        playListId = MyPlaybackService.PLAY_STACK_ID,
//                        mediaStoreId = track.mediaStoreId,
//                        orderInPlaylist = index
//                    )
//                }
//
//                playlistDao.insertAllTracksToPlaylist(updateQueue)
//            }
        }
    }

    suspend fun addTrackToPlayStack(track: Track) {
        withContext(Dispatchers.IO) {
            trackDao.insertTrack(track)
        }
    }

}