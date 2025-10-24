package io.axept.samplekotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics.ConsentStatus
import com.google.firebase.analytics.FirebaseAnalytics.ConsentType
import com.google.firebase.analytics.analytics
import io.axept.android.googleconsent.GoogleConsentStatus
import io.axept.android.googleconsent.GoogleConsentType
import io.axept.android.library.AxeptioEventListener
import io.axept.android.library.AxeptioSDK
import io.axept.samplekotlin.config.ConfigurationManager
import io.axept.samplekotlin.navigation.AppNavHost
import io.axept.samplekotlin.ui.theme.SampleKotlinTheme

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