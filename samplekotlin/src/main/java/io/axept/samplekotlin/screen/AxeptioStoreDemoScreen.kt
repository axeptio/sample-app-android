package io.axept.samplekotlin.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.axept.android.library.AxeptioSDK
import io.axept.android.library.AxeptioStore
import io.axept.samplekotlin.MainActivity

/**
 * Demonstrates the StateFlow-based [AxeptioStore] for reactive consent state in Jetpack Compose.
 *
 * Analogue of the iOS `SwiftUISampleView`. The screen registers an [AxeptioStore] as an event
 * listener for the duration it is on-screen and surfaces every flow it exposes — Google Consent Mode
 * v2 map, the popup-closed / consent-cleared / CMP-restored / consent-saved counters, and the
 * SDK-level error channel added in 2.2.0.
 *
 * As of SDK 2.5.0 the store covers every [io.axept.android.library.AxeptioEventListener] callback,
 * so a single registration is enough — earlier versions had no flow for `onCMPRestored` and this
 * screen had to register a second, plain listener alongside the store to observe it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AxeptioStoreDemoScreen(onBackClick: () -> Unit) {
    val activity = LocalContext.current as MainActivity
    val store = remember { AxeptioStore() }

    DisposableEffect(store) {
        AxeptioSDK.instance().setEventListener(store)
        onDispose { AxeptioSDK.instance().removeEventListener(store) }
    }

    val googleConsent by store.googleConsent.collectAsState()
    val popupClosedCount by store.popupClosedEventCount.collectAsState()
    val consentClearedCount by store.consentClearedEventCount.collectAsState()
    val cmpRestoredCount by store.cmpRestoredEventCount.collectAsState()
    val consentSavedCount by store.consentSavedEventCount.collectAsState()
    val error by store.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AxeptioStore (Compose) Demo") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "This screen shows how to consume AxeptioStore's StateFlow API from a Compose UI. The store is registered as an event listener while this screen is on-screen and unregistered on dispose.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(colors = CardDefaults.cardColors()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Event counters", style = MaterialTheme.typography.titleMedium)
                    Text("Popup closed: $popupClosedCount")
                    Text("Consent cleared: $consentClearedCount")
                    // Fires once per silent restoration — on init, on foreground, and after network
                    // recovery — so this climbs past 1 over a session without any popup being shown.
                    Text("CMP restored (silent): $cmpRestoredCount")
                    // The popup counterpart: one increment per decision the user saves, so this only
                    // moves when a popup was actually shown and answered.
                    Text("Consent saved: $consentSavedCount")
                }
            }

            Card(colors = CardDefaults.cardColors()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Google Consent Mode v2", style = MaterialTheme.typography.titleMedium)
                    if (googleConsent.isEmpty()) {
                        Text("No consent received yet.")
                    } else {
                        googleConsent.forEach { (type, status) ->
                            Text("${type.name}: ${status.name}")
                        }
                    }
                }
            }

            Card(colors = CardDefaults.cardColors()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("SDK error channel", style = MaterialTheme.typography.titleMedium)
                    Text(error ?: "No error.")
                    if (error != null) {
                        OutlinedButton(onClick = { store.clearError() }) {
                            Text("Dismiss error")
                        }
                    }
                }
            }

            Button(
                onClick = { AxeptioSDK.instance().showConsentScreen(activity, true) },
                colors = ButtonDefaults.buttonColors()
            ) {
                Text("Show consent screen")
            }

            OutlinedButton(onClick = { AxeptioSDK.instance().clearConsents() }) {
                Text("Clear consents")
            }
        }
    }
}
