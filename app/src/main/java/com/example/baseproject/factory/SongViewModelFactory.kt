package com.example.baseproject.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baseproject.viewmodel.SongViewModel

class SongViewModelFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
            return SongViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}