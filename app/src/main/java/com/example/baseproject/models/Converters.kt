package com.example.baseproject.models

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun toUri(value: String?): Uri? {
        return value?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }
}