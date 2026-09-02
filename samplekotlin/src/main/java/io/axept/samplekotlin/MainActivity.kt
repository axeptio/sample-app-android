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
import io.axept.samplekotlin.config.ConfigurationManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get configuration from ConfigurationManager instead of BuildConfig
        val configManager = ConfigurationManager.getInstance(this)
        val currentConfig = configManager.currentConfiguration
        val targetService = currentConfig.targetService

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
            clientId = currentConfig.clientId,
            cookiesVersion = currentConfig.cookiesVersion,
            token = currentConfig.token,
            widgetType = currentConfig.widgetType,
            prId = currentConfig.prId,
            consentExpirationDays = currentConfig.consentExpirationDays,
            shouldUpdateConsentExpiration = currentConfig.shouldUpdateConsentExpiration,
        )

        // Applies setForceShowConsentDebug() and setDisplayPopUpOnEnterForeground() from the stored
        // configuration. Both are runtime setters, so the Configuration screen re-applies them on
        // every change rather than waiting for the next app start.
        configManager.applyPopupSettingsToSdk()

        AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
            // Since SDK 2.3.0 the SDK forwards Google Consent Mode v2 signals to Firebase Analytics
            // itself (codeless, via reflection), so this mapping is no longer required — it is kept
            // here as the explicit reference implementation and to prove the callback fires.
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

            // Added in SDK 2.4.0, fixed in 2.5.0 — earlier releases never fired it on the successful
            // restore path. Fires once per silent restoration of existing, still-valid consent —
            // i.e. no popup was shown. The SDK restores more than once per session (on initialize,
            // on return to foreground, and after network recovery), so expect this repeatedly rather
            // than only at startup. Consent is readable via getVendorConsents() by the time it fires.
            override fun onCMPRestored() {
                Log.d(TAG, "Consent silently restored — ${AxeptioSDK.instance().getConsentedVendors().size} vendors consented")
            }

            // New in SDK 2.5.0. The counterpart to onCMPRestored(): fires once per consent decision
            // the user saves in the popup — accept, refuse or customise — after the decision has been
            // persisted, so getVendorConsents() already reflects it here. Never fires for a silent
            // restoration, and is not replayed to listeners registered after the decision was made.
            override fun onConsentSaved() {
                Log.d(TAG, "Consent saved by the user — ${AxeptioSDK.instance().getConsentedVendors().size} vendors consented")
            }

            override fun onError(message: String) {
                Log.e(TAG, "Axeptio SDK error: $message")
            }
        })
    }
}

val TAG = "axeptio-sample-kotlin"