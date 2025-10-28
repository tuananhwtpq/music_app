package com.example.baseproject.utils.ex

import android.view.View
import com.example.baseproject.utils.UnDoubleClick

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setOnUnDoubleClick(interval: Long = 500L, onViewClick: (View?) -> Unit) {
    setOnClickListener(UnDoubleClick(defaultInterval = interval, onViewClick = onViewClick))
}