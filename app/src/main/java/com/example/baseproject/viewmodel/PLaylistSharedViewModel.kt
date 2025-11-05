package com.example.baseproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PLaylistSharedViewModel : ViewModel() {

    private val _isCreateNewPlaylist = MutableLiveData<Boolean?>(false)
    val isCreateNewPLaylist: MutableLiveData<Boolean?> = _isCreateNewPlaylist

    fun createNewPlaylist() {
        _isCreateNewPlaylist.value = true
    }

    fun doneCreatingNewPlaylist() {
        _isCreateNewPlaylist.value = null
    }
}