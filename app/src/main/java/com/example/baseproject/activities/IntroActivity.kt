package com.example.baseproject.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.util.Log
import android.window.OnBackInvokedCallback
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.example.baseproject.adapters.IntroViewPagerAdapter
import com.example.baseproject.bases.BaseActivity
import com.example.baseproject.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity<ActivityIntroBinding>(ActivityIntroBinding::inflate) {

    private var mAdapter: IntroViewPagerAdapter? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.vpIntro.currentItem in 1..2) {
                binding.vpIntro.currentItem -= 1
            } else {
                finish()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                goToHome()
            } else {
                showPermissionRequest()
            }
        }

    override fun initData() {
        mAdapter = IntroViewPagerAdapter(this)
    }

    override fun initView() {
        Log.d("Test", "Intro Activity")
        binding.vpIntro.adapter = mAdapter

        binding.btnNext.setOnClickListener {
            when (binding.vpIntro.currentItem) {
                0 -> {
                    binding.vpIntro.currentItem = 1
                }

                1 -> {
                    binding.vpIntro.currentItem = 2
                }

                2 -> {

                    val permisstion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    requestPermissionLauncher.launch(permisstion)
                }
            }
        }

        val onPageChangeCallback: ViewPager2.OnPageChangeCallback =
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }
            }
        binding.vpIntro.registerOnPageChangeCallback(onPageChangeCallback)
        binding.dotIndicator.attachTo(binding.vpIntro)

    }

    private fun showPermissionRequest() {

        AlertDialog.Builder(this)
            .setTitle("This app need this permission")
            .setMessage("this app need this permission")
            .setPositiveButton("OK") { _, _ ->
                val permissionToRequest =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                requestPermissionLauncher.launch(permissionToRequest)
            }
            .setNegativeButton("CANCEL", null)
            .show()

    }

    override fun initActionView() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun goToHome() {
        startActivity(Intent(this@IntroActivity, MainActivity::class.java))
        finish()
    }
}