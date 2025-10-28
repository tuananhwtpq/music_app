package com.example.baseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseproject.database.SongsDatabase
import com.example.baseproject.models.Track
import com.example.baseproject.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongViewModel(application: Application) : AndroidViewModel(application) {

//    private val _trackList = MutableLiveData<List<Track>>(emptyList())
//    val trackList: LiveData<List<Track>> = _trackList

    private var trackRepository: TrackRepository

    init {
        val db = SongsDatabase.getInstance(application)
        trackRepository = TrackRepository(db.trackDao(), application.applicationContext)
    }

    val trackList: LiveData<List<Track>> = trackRepository.allTrack.asLiveData()

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            trackRepository.asyncDataFromMediaStore()
        }
    }

//    fun loadSongs() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val trackList = mutableListOf<Track>()
//
//            val projection = arrayOf(
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.Media.DURATION,
//                MediaStore.Audio.Media.ALBUM_ID
//            )
//
//            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
//
//            getApplication<Application>().contentResolver.query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                projection,
//                selection,
//                null,
//                null
//            )?.use { cursor ->
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
//                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
//                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
//
//                while (cursor.moveToNext()) {
//                    val id = cursor.getLong(idColumn)
//                    val title = cursor.getString(titleColumn)
//                    val artist = cursor.getString(artistColumn)
//                    val duration = cursor.getLong(durationColumn)
//                    val albumId = cursor.getLong(albumIdColumn)
//
//                    val contentUri = ContentUris.withAppendedId(
//                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
//                    )
//
//                    val albumArtUri = ContentUris.withAppendedId(
//                        Uri.parse("content://media/external/audio/albumart"), albumId
//                    )
//
//                    trackList.add(
//                        Track(
//                            mediaStoreId = id,
//                            title = title,
//                            artist = artist,
//                            duration = duration,
//                            uri = contentUri,
//                            albumArtUri = albumArtUri,
//                            album = null,
//                            genre = null,
//                            isFavorite = false,
//                            dateAdded = null,
//                            year = null,
//                        )
//                    )
//                }
//            }
//            _trackList.postValue(trackList)
//        }
//    }
}