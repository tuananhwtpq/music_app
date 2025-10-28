package com.example.baseproject.adapters

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.baseproject.fragments.FoldersFragment
import com.example.baseproject.fragments.LibraryFragment
import com.example.baseproject.fragments.PlaylistsFragment
import com.example.baseproject.fragments.SongsFragment
import kotlin.jvm.Throws

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> SongsFragment()
            1 -> PlaylistsFragment()
            2 -> SongsFragment()
            3 -> LibraryFragment()
            4 -> FoldersFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

}