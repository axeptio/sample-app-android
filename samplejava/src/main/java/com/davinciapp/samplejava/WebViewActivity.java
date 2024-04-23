package com.davinciapp.samplejava;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.axept.android.library.AxeptioSDK;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        String token = getIntent().getStringExtra(ARG_TOKEN);

        if (token == null || token.isEmpty()) {
            token = AxeptioSDK.instance().getToken();
            if (token == null) {
                token = "";
            }
        }


        WebView webview = findViewById(R.id.web_view);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setDomStorageEnabled(true);

        String baseUrl = "https://google-cmp-partner.axept.io/cmp-for-publishers.html";
        Uri url = AxeptioSDK.instance().appendAxeptioToken(Uri.parse(baseUrl), token);
        webview.loadUrl(url.toString());
    }

    static String ARG_TOKEN = "token";
}
