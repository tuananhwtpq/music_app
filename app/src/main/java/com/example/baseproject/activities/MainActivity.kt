package com.example.baseproject.activities

import android.Manifest
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
import com.example.baseproject.R
import com.example.baseproject.bases.BaseActivity
import com.example.baseproject.databinding.ActivityMainBinding
import java.security.Permissions

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

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

    }

    override fun initView() {
        Log.d("Test", "MainActivity")

        setupController()
        setupDestination(navController)
        setupDrawerLayout()
        setupItemToolBar()
        handleBottomNavView()
        handleItemBottomNavClicked()
        showPermissionRequest()

        NavigationUI.setupWithNavController(binding.bottomNavView, navController)

    }

    private fun setupController() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container_view) as NavHostFragment
        navController = navHostFragment.navController
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

    private fun setupDestination(navController: NavController) {

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment,
                R.id.introActivity -> {
                    binding.toolBar.toolBarMain.visibility = View.GONE
                    binding.coordinatorLayout.visibility = View.GONE
                }

                R.id.songsFragment,
                R.id.playlistsFragment,
                R.id.libraryFragment,
                R.id.foldersFragment -> {
                    binding.toolBar.toolBarMain.visibility = View.VISIBLE
                    binding.coordinatorLayout.visibility = View.VISIBLE
                }

                else -> {
                    binding.toolBar.toolBarMain.visibility = View.GONE
                    binding.coordinatorLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun handleItemBottomNavClicked() {
    }

    private fun handleBottomNavView() {
        binding.bottomNavView.background = null
        binding.bottomNavView.menu.getItem(2).isEnabled = false
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