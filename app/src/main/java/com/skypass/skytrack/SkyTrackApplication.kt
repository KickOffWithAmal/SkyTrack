package com.skypass.skytrack

import android.app.Application
import com.skypass.skytrack.data.sync.OfflineSyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SkyTrackApplication : Application() {

    @Inject
    lateinit var offlineSyncManager: OfflineSyncManager

    override fun onCreate() {
        super.onCreate()
        offlineSyncManager.startObserving()
    }
}
