package io.axept.samplekotlin.screen

import android.net.Uri
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import io.axept.android.library.Axeptio
import io.axept.android.library.AxeptioSDK

@Composable
internal fun WebViewScreen(
    customToken: String?,
    onBack: () -> Unit,
    axeptio: Axeptio = AxeptioSDK.instance()
) {
    val url = axeptio.appendAxeptioToken(
        uri = Uri.parse("https://google-cmp-partner.axept.io/cmp-for-publishers.html"),
        token = customToken ?: axeptio.token ?: ""
    )
    val state = rememberWebViewState(url.toString())

    Scaffold(
        topBar = {
            TopBar(back = onBack)
        }
    ) { innerPadding ->
        WebView(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state = state,
            onCreated = {
                it.settings.javaScriptEnabled = true
                it.settings.domStorageEnabled = true
            },
            client = object : AccompanistWebViewClient() {
                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    view.evaluateJavascript("""localStorage.clear();""".trimIndent(), null)
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(back: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = back) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
            }
        }
    )
}