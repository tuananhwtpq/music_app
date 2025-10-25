package com.example.baseproject.activities

import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import com.example.baseproject.R
import com.example.baseproject.adapters.MainViewPagerAdapter
import com.example.baseproject.bases.BaseActivity
import com.example.baseproject.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.security.Permissions

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var toggle: ActionBarDrawerToggle
    private var mAdapter: MainViewPagerAdapter? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //showPermissionRequest()
                Log.d(TAG, "Permission granted")
            } else {
                //showPermissionRequest()
            }
        }

    override fun initData() {
        mAdapter = MainViewPagerAdapter(this)

    }

    override fun initView() {
        Log.d("Test", "MainActivity")
        binding.mainViewPager.adapter = mAdapter

        setupTabLayout()
        showPermissionRequest()
        setupDrawerLayout()
        setupItemToolBar()

        val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        }

        binding.mainViewPager.registerOnPageChangeCallback(onPageChangeCallback)

    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.mainTabsHolder, binding.mainViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Songs"
                    tab.setIcon(R.drawable.outline_ad_24)
                }

                1 -> {
                    tab.text = "Playlists"
                    tab.setIcon(R.drawable.outline_ad_24)
                }

                2 -> {
                    tab.text = "Placeholders"
                    tab.setIcon(R.drawable.outline_ad_24)
                }

                3 -> {
                    tab.text = "Library"
                    tab.setIcon(R.drawable.outline_ad_24)
                }

                4 -> {
                    tab.text = "Folders"
                    tab.setIcon(R.drawable.outline_ad_24)
                }
            }
        }.attach()
    }


    private fun showPermissionRequest() {
        AlertDialog.Builder(this)
            .setTitle("Notification permission")
            .setMessage("We need notification permission to show media controller on notification when you play music")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Later", null)
            .show()
    }

    private fun setupItemToolBar() {

        val btnMenu = binding.toolBar.btnMenu
        val btnSearch = binding.toolBar.btnSearch

        btnMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupDrawerLayout() {

        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, R.string.nav_open, R.string.nav_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setSupportActionBar(binding.toolBar.toolBarMain)

        val drawerWidth = 350
        val drawerHeight = 50
        val destiny = resources.displayMetrics.density
        val slideRangeX = (drawerWidth * destiny).toInt()
        val slideRangeY = (drawerHeight * destiny).toInt()

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val slideX = slideRangeX * slideOffset
                val slideY = slideRangeY * slideOffset
                binding.mainLayout.translationX = slideX
                binding.mainLayout.translationY = slideY
            }

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }


    override fun initActionView() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

}