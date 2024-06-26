package com.ns.turkcellfinal.core.app

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TurkcellApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0 // 1 saatlik fetch intervali
        }

        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(configSettings)
    }
}