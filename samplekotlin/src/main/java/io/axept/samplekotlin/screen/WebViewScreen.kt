package io.axept.samplekotlin.screen

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import io.axept.android.library.Axeptio
import io.axept.android.library.AxeptioSDK

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun WebViewScreen(
    customToken: String?,
    onBack: () -> Unit,
    axeptio: Axeptio = AxeptioSDK.instance()
) {
    val context = LocalContext.current

    val url by remember(customToken, axeptio.token) {
        mutableStateOf(
            axeptio.appendAxeptioToken(
                uri = "https://google-cmp-partner.axept.io/cmp-for-publishers.html".toUri(),
                token = customToken ?: axeptio.token.orEmpty()
            ).toString()
        )
    }

    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    view.evaluateJavascript("localStorage.clear();", null)
                }
            }
        }
    }

    Scaffold(
        topBar = { TopBar(back = onBack) }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            factory = { webView },
            update = { webView ->
                if(webView.url != url) webView.loadUrl(url)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(back: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = back) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back"
                )
            }
        }
    )
}
