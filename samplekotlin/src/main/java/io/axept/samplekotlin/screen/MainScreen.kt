package io.axept.samplekotlin.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.axept.android.library.AxeptioEventListener
import io.axept.android.library.AxeptioSDK
import io.axept.android.library.AxeptioService
import io.axept.samplekotlin.MainActivity
import io.axept.samplekotlin.TAG
import io.axept.samplekotlin.ui.theme.Red
import io.axept.samplekotlin.ui.theme.Yellow

@Composable
fun MainScreen(
    targetService: AxeptioService,
    onOpenWebView: (token: String) -> Unit
) {
    val activity = LocalContext.current as MainActivity
    val viewModel: MainViewModel =
        viewModel(factory = MainViewModel.Companion.Factory(activity.application))

    var interstitialAd: InterstitialAd? = remember {
        null
    }

    val prefState = viewModel.state.collectAsState()
    val adState = viewModel.adState.collectAsState()

    val showPreferencesPopup = remember {
        mutableStateOf(false)
    }
    val showTokenInputPopup = remember {
        mutableStateOf(false)
    }
    val shouldLoadAdd = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        viewModel.setTargetService(targetService)
    }

    Scaffold(
        topBar = {
            AxeptioTopBar()
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AxeptioButton(
                        label = "Consent pop up",
                        onClick = {
                            AxeptioSDK.instance().showConsentScreen(activity, true)
                        })

                    AxeptioButton(label = "Shared Preferences", onClick = {
                        showPreferencesPopup.value = true
                    })

                    AxeptioButton(
                        label = "Google ad",
                        enabled = adState.value.status == MainViewModel.AddStateUI.Status.LOADED,
                        onClick = { interstitialAd?.show(activity) },
                        loading = adState.value.status == MainViewModel.AddStateUI.Status.LOADING
                    )

                    AxeptioButton(
                        label = "Shared consents web view",
                        onClick = {
                            showTokenInputPopup.value = true
                        }
                    )

                    AxeptioButton(
                        label = "Clear consent",
                        onClick = {
                            AxeptioSDK.instance().clearConsents()
                            shouldLoadAdd.value++
                        },
                        color = Red
                    )

                }

                if (showPreferencesPopup.value) {
                    Dialog(onDismissRequest = { showPreferencesPopup.value = false }) {
                        PreferencesDisplay(state = prefState.value)
                    }
                }

                if (showTokenInputPopup.value) {
                    TokenInputPopup(
                        onConfirm = { onOpenWebView(it) },
                        onDismiss = {showTokenInputPopup.value = false}
                    )
                }
            }
        }

        //-- Google add
        LaunchedEffect(shouldLoadAdd.value) {
            loadAd(
                context = activity,
                onAddStatusChanged = { viewModel.setAdStatus(it) },
                onFailed = { interstitialAd = null },
                onLoaded = {
                    interstitialAd = it
                    setAdEventCallback(
                        interstitialAd = interstitialAd,
                        onDismiss = {
                            interstitialAd = null
                            shouldLoadAdd.intValue++
                        })
                }
            )
        }

        LaunchedEffect(Unit) {
            AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
                override fun onPopupClosedEvent() {
                    shouldLoadAdd.intValue++
                }
            })
        }
        //--
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AxeptioTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Axeptio | Kotlin Sample",
                style = MaterialTheme.typography.headlineMedium
            )
        },
    )
}

@Composable
internal fun AxeptioButton(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    loading: Boolean = false,
    color: Color = Yellow,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .padding(bottom = 24.dp)
            .width(250.dp)
            .height(56.dp),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors().copy(containerColor = color)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge
            )
        }

    }
}

//--------------------------------------- G O O G L E  A D  --------------------------------------//

private fun loadAd(
    context: Context,
    onAddStatusChanged: (MainViewModel.AddStateUI.Status) -> Unit,
    onFailed: () -> Unit,
    onLoaded: (InterstitialAd) -> Unit
) {
    onAddStatusChanged(MainViewModel.AddStateUI.Status.LOADING)

    InterstitialAd.load(
        context,
        "ca-app-pub-3940256099942544/1033173712",
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                onAddStatusChanged(MainViewModel.AddStateUI.Status.FAILURE)
                onFailed()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                onAddStatusChanged(MainViewModel.AddStateUI.Status.LOADED)
                onLoaded(interstitialAd)
            }
        })
}

private fun setAdEventCallback(interstitialAd: InterstitialAd?, onDismiss: () -> Unit) {
    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

        override fun onAdDismissedFullScreenContent() {
            // Load a new add on dismissed
            onDismiss()
        }

        override fun onAdFailedToShowFullScreenContent(e: AdError) {
            super.onAdFailedToShowFullScreenContent(e)
            Log.d(TAG, "AD FAILURE: \n$e")
        }
    }
}