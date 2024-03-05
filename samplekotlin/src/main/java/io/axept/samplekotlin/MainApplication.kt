package io.axept.samplekotlin

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.initialize

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(this)

        MobileAds.initialize(this)
    }

}