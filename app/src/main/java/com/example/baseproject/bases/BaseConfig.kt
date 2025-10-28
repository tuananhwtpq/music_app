package com.example.baseproject.bases

import android.content.Context
import com.example.baseproject.utils.ex.getSharedPrefs

open class BaseConfig(val context: Context) {

    protected val prefs = context.getSharedPrefs()

    companion object {
        fun newInstance(context: Context) = BaseConfig(context)
    }
}