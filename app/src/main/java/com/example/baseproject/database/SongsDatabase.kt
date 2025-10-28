package com.example.baseproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.baseproject.interfaces.PlaylistDao
import com.example.baseproject.interfaces.TrackDao
import com.example.baseproject.models.Converters
import com.example.baseproject.models.PlayListSongCrossRef
import com.example.baseproject.models.Playlist
import com.example.baseproject.models.Track

@Database(
    entities = [Track::class, Playlist::class, PlayListSongCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SongsDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    companion object {
        private var db: SongsDatabase? = null

        fun getInstance(context: Context): SongsDatabase {
            if (db == null) {
                synchronized(SongsDatabase::class) {

                    if (db == null) {
                        db = Room.databaseBuilder(
                            context.applicationContext,
                            SongsDatabase::class.java,
                            "songs_db"
                        ).build()
                    }

                }
            }

            return db!!
        }

        fun destroyInstance() {
            db = null
        }

    }
}