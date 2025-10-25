package com.example.baseproject.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.baseproject.R
import com.example.baseproject.activities.LanguageActivity
import com.example.baseproject.bases.BaseFragment
import com.example.baseproject.databinding.FragmentSplashBinding
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.invisible
import com.example.baseproject.utils.visible
import com.snake.squad.adslib.AdmobLib
import com.snake.squad.adslib.cmp.GoogleMobileAdsConsentManager
import com.snake.squad.adslib.utils.AdsHelper
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess


class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {


    private var isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var isInitAds = AtomicBoolean(false)

    override fun initData() {
//        if (!isTaskRoot
//            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
//            && intent.action != null
//            && intent.action == Intent.ACTION_MAIN
//        ) {
//            finish()
//            return
//        }
    }

    override fun initView() {

        Log.d("Test", "Splash fragment")

        if (AdsHelper.isNetworkConnected(requireActivity())) {
            binding.tvLoadingAds.visible()
            setupCMP()
//            initRemoteConfig()
        } else {
            binding.tvLoadingAds.invisible()
            Handler(Looper.getMainLooper()).postDelayed({
                replaceActivity()
            }, 3000)
        }
    }

    override fun initActionView() {
        //onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun setupCMP() {
        val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager(requireActivity())
        googleMobileAdsConsentManager.gatherConsent { error ->
            error?.let {
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.get()) {
            //start action
            return
        }
        isMobileAdsInitializeCalled.set(true)
        initAds()
    }

//    private fun initRemoteConfig() {
//        RemoteConfig.initRemoteConfig(this, initListener = object : RemoteConfig.InitListener {
//            override fun onComplete() {
//                RemoteConfig.getAllRemoteValueToLocal()
//                if (isInitAds.get()) {
//                    return
//                }
//                isInitAds.set(true)
//                setupCMP()
//            }
//
//            override fun onFailure() {
//                RemoteConfig.getDefaultRemoteValue()
//                setupCMP()
//            }
//        })
//    }

    private fun initAds() {
        AdmobLib.initialize(
            requireActivity(),
            isDebug = true,
            isShowAds = false,
            onInitializedAds = {
                if (it) {
                    // todo: fix here
                    binding.tvLoadingAds.invisible()
                    Handler(Looper.getMainLooper()).postDelayed({
                        replaceActivity()
                    }, 5000)
                } else {
                    binding.tvLoadingAds.invisible()
                    Handler(Looper.getMainLooper()).postDelayed({
                        replaceActivity()
                    }, 5000)
                }
            })
    }

    private fun replaceActivity() {

        findNavController().navigate(R.id.action_splashFragment_to_songsFragment)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            exitProcess(0)
        }
    }

}