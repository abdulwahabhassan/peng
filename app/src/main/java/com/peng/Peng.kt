package com.peng

import android.app.Application
import androidx.viewbinding.BuildConfig
import co.paystack.android.PaystackSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Peng: Application() {

    override fun onCreate() {
        super.onCreate()
        initTimberLog()
        PaystackSdk.initialize(applicationContext);
    }
    private fun initTimberLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}