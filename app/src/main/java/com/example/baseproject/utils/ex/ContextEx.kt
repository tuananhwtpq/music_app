package com.example.baseproject.utils.ex

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.baseproject.utils.Config
import com.example.baseproject.utils.Constants.PREFS_KEY

val Context.config: Config get() = Config.Companion.newInstance(applicationContext)

fun Context.getSharedPrefs() = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}


//fun Context.isTabVisible(flag: Int) = config.showTabs and flag != 0

//fun Context.getVisibleTabs() = Constants.tabList.filter { isTabVisible(it) }