package com.example.baseproject.fragments

import androidx.viewpager2.widget.ViewPager2
import com.example.baseproject.adapters.LibViewPagerAdapter
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : BaseFragment<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    private var libraryAdapter: LibViewPagerAdapter? = null

    override fun initData() {

        libraryAdapter = LibViewPagerAdapter(this)
    }

    override fun initView() {

        binding.libViewPager.adapter = libraryAdapter

        setupTabLayout()

        val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        }

        binding.libViewPager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun initActionView() {
    }

    fun setupTabLayout() {

        TabLayoutMediator(binding.mainTabsHolder, binding.libViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Album"
                }

                1 -> {
                    tab.text = "Artist"
                }
            }
        }.attach()
    }

}