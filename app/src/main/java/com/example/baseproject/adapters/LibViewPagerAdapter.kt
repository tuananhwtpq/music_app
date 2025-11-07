package com.example.baseproject.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.baseproject.fragments.AlbumFragment
import com.example.baseproject.fragments.ArtistFragment
import com.example.baseproject.fragments.LibraryFragment

class LibViewPagerAdapter(libraryFragment: LibraryFragment) :
    FragmentStateAdapter(libraryFragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AlbumFragment()
            1 -> ArtistFragment()
            else -> throw IllegalArgumentException("Invalid position when creating fragment")
        }
    }

    override fun getItemCount(): Int = 2


}