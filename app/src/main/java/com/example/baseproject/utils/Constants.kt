package com.example.baseproject.utils

object Constants {

    const val LANGUAGE_EXTRA = "language_extra"
    const val HAWK_LANGUAGE_POSITION = "LANGUAGE"
    const val PRIVACY_POLICY = ""
    const val PREFS_KEY = "Prefs"

//    var showTabs: Int
//        get() = prefs.getInt(SHOW_TABS, ALL_TABS_MASK)
//        set(showTabs) = prefs.edit().putInt(SHOW_TABS, showTabs).apply()
//
//    const val ALL_TABS_MASK = TAB_PLAYLISTS or TAB_FOLDERS or TAB_ARTISTS or TAB_ALBUMS or TAB_TRACKS

    const val TAB_PLAYLISTS = 1
    const val TAB_FOLDERS = 2
    const val TAB_ARTISTS = 4
    const val TAB_ALBUMS = 8
    const val TAB_TRACKS = 16

    val tabList: ArrayList<Int>
        get() = arrayListOf(
            TAB_PLAYLISTS,
            TAB_FOLDERS,
            TAB_ARTISTS,
            TAB_ALBUMS,
            TAB_TRACKS
        )

}