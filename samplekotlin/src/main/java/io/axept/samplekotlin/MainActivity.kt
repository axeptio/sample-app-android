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
import io.axept.samplekotlin.navigation.AppNavHost
import io.axept.samplekotlin.screen.MainScreen
import io.axept.samplekotlin.screen.MainViewModel
import io.axept.samplekotlin.ui.theme.SampleKotlinTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SampleKotlinTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }

        AxeptioSDK.instance().initialize(
            activity = this,
            clientId = "5fbfa806a0787d3985c6ee5f",
            cookiesVersion = "google cmp partner program sandbox-en-EU",
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