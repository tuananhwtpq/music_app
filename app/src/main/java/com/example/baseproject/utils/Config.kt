package com.example.baseproject.utils

import android.content.Context
import com.example.baseproject.bases.BaseConfig

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }
}