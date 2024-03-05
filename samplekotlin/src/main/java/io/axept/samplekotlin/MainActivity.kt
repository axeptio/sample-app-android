package io.axept.samplekotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import io.axept.samplekotlin.screen.MainScreen
import io.axept.samplekotlin.screen.MainViewModel
import io.axept.samplekotlin.ui.theme.SampleKotlinTheme

class MainActivity : ComponentActivity() {

    private val TAG = "axeptio-sample-kotlin"
    private var interstitialAd: InterstitialAd? = null

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Companion.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            SampleKotlinTheme {
                MainScreen(
                    viewModel = viewModel,
                    onShowAd = {
                        interstitialAd?.show(this)
                    }
                )
            }
        }

        AxeptioSDK.instance().initialize(
            activity = this,
            configurationId = "62ac903ddf8cf90ca29d9585",
            projectId = "5fbfa806a0787d3985c6ee5f"
        )

        AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
            override fun onPopupClosedEvent() {
                loadAd()
            }
        })

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

        loadAd()

    }

    private fun loadAd() {
        viewModel.setAdStatus(MainViewModel.AddStateUI.Status.LOADING)

        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    viewModel.setAdStatus(MainViewModel.AddStateUI.Status.FAILURE)
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    viewModel.setAdStatus(MainViewModel.AddStateUI.Status.LOADED)
                    this@MainActivity.interstitialAd = interstitialAd
                    setAdEventCallback()
                }
            })
    }

    private fun setAdEventCallback() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                // Load a new add on dismissed
                interstitialAd = null
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                super.onAdFailedToShowFullScreenContent(e)
                Log.d(TAG, "AD FAILURE: \n$e")
            }
        }
    }
}