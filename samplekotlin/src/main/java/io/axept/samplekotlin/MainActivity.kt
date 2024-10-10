package io.axept.samplekotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics.ConsentStatus
import com.google.firebase.analytics.FirebaseAnalytics.ConsentType
import com.google.firebase.analytics.analytics
import io.axept.android.googleconsent.GoogleConsentStatus
import io.axept.android.googleconsent.GoogleConsentType
import io.axept.android.library.AxeptioEventListener
import io.axept.android.library.AxeptioSDK
import io.axept.android.library.AxeptioService
import io.axept.samplekotlin.navigation.AppNavHost
import io.axept.samplekotlin.screen.MainScreen
import io.axept.samplekotlin.screen.MainViewModel
import io.axept.samplekotlin.ui.theme.SampleKotlinTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val targetService =
            if (BuildConfig.AXEPTIO_TARGET_SERVICE == "publishers") AxeptioService.PUBLISHERS_TCF
            else AxeptioService.BRANDS

        setContent {
            SampleKotlinTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    targetService = targetService
                )
            }
        }


        AxeptioSDK.instance().initialize(
            activity = this,
            targetService = targetService,
            clientId = BuildConfig.AXEPTIO_CLIENT_ID,
            cookiesVersion = BuildConfig.AXEPTIO_COOKIES_VERSION,
            token = null
        )

        // Google consent v2 implementation
        AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
            override fun onGoogleConsentModeUpdate(consentMap: Map<GoogleConsentType, GoogleConsentStatus>) {
                val firebaseConsentMap = consentMap.entries.associate { (type, status) ->
                    val firebaseConsentType = when (type) {
                        GoogleConsentType.ANALYTICS_STORAGE -> ConsentType.ANALYTICS_STORAGE
                        GoogleConsentType.AD_STORAGE -> ConsentType.AD_STORAGE
                        GoogleConsentType.AD_USER_DATA -> ConsentType.AD_USER_DATA
                        GoogleConsentType.AD_PERSONALIZATION -> ConsentType.AD_PERSONALIZATION
                    }

                    val firebaseConsentStatus = when (status) {
                        GoogleConsentStatus.GRANTED -> ConsentStatus.GRANTED
                        GoogleConsentStatus.DENIED -> ConsentStatus.DENIED
                    }

                    firebaseConsentType to firebaseConsentStatus
                }
                Firebase.analytics.setConsent(firebaseConsentMap)
            }
        })
    }
}

val TAG = "axeptio-sample-kotlin"