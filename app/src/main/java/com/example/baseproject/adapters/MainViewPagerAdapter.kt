package com.example.baseproject.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.baseproject.fragments.FoldersFragment
import com.example.baseproject.fragments.LibraryFragment
import com.example.baseproject.fragments.PlaylistsFragment
import com.example.baseproject.fragments.SongsFragment
import com.example.baseproject.fragments.VisualizerFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> SongsFragment()
            1 -> PlaylistsFragment()
            2 -> VisualizerFragment()
            3 -> LibraryFragment()
            4 -> FoldersFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

}