package io.axept.samplekotlin.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.axept.android.library.AxeptioSDK
import io.axept.samplekotlin.MainActivity

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onShowAd: () -> Unit
) {
    val activity = LocalContext.current as MainActivity

    val prefState = viewModel.state.collectAsState()
    val adState = viewModel.adState.collectAsState()

    var showPreferencesPopup = remember {
        mutableStateOf(false)
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
                            AxeptioSDK.instance().showConsentScreen(activity)
                        })

                    AxeptioButton(label = "Shared Preferences", onClick = {
                        showPreferencesPopup.value = true
                    })

                    AxeptioButton(
                        label = "Google ad",
                        enabled = adState.value.status == MainViewModel.AddStateUI.Status.LOADED,
                        onClick = onShowAd,
                        loading = adState.value.status == MainViewModel.AddStateUI.Status.LOADING
                    )


                }

                if (showPreferencesPopup.value) {
                    Dialog(onDismissRequest = { showPreferencesPopup.value = false }) {
                        PreferencesDisplay(state = prefState.value)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AxeptioTopBar() {
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
fun AxeptioButton(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .padding(bottom = 24.dp)
            .width(250.dp)
            .height(56.dp),
        enabled = enabled,
        onClick = onClick
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