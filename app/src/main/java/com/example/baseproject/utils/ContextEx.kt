package com.example.baseproject.utils

import android.content.Context
import com.example.baseproject.utils.Constants.PREFS_KEY

val Context.config: Config get() = Config.newInstance(applicationContext)

fun Context.getSharedPrefs() = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)


//fun Context.isTabVisible(flag: Int) = config.showTabs and flag != 0

//fun Context.getVisibleTabs() = Constants.tabList.filter { isTabVisible(it) }