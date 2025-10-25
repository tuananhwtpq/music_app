package com.example.baseproject

import com.example.baseproject.utils.SharedPrefManager
import com.snake.squad.adslib.AdsApplication

class MyApplication: AdsApplication("", isProduction = true, isEnabledAdjust = false) {

    override fun onCreate() {
        super.onCreate()
        SharedPrefManager.init(this)
    }

}