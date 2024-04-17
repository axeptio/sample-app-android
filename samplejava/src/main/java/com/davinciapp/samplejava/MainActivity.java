package com.davinciapp.samplejava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

import io.axept.android.googleconsent.GoogleConsentStatus;
import io.axept.android.googleconsent.GoogleConsentType;
import io.axept.android.library.Axeptio;
import io.axept.android.library.AxeptioEventListener;
import io.axept.android.library.AxeptioSDK;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this);

        Axeptio axeptio = AxeptioSDK.instance();
        axeptio.initialize(
                MainActivity.this,
                "5fbfa806a0787d3985c6ee5f",
                "google cmp partner program sandbox-en-EU",
                null
        );
        setGoogleConsent();

        mViewModel = new ViewModelProvider(this, new MainViewModel.Factory(getApplication())).get(MainViewModel.class);

        Button consentPopupBtn = findViewById(R.id.btn_popup);
        Button adBtn = findViewById(R.id.btn_ad);
        Button preferencesBtn = findViewById(R.id.btn_preferences);
        Button clearConsentsBtn = findViewById(R.id.btn_clear_consents);
        Button sharedConsentsUrlBtn = findViewById(R.id.btn_open_url);
        ConstraintLayout loaderLayout = findViewById(R.id.loader_layout);

        mViewModel.loadingAd.observe(this, loading -> {
            if (loading) loaderLayout.setVisibility(View.VISIBLE);
            else loaderLayout.setVisibility(View.GONE);
        });

        consentPopupBtn.setOnClickListener(view -> axeptio.showConsentScreen(this));
        adBtn.setOnClickListener(view -> loadAd());
        preferencesBtn.setOnClickListener(view -> showPreferencesDialog());
        clearConsentsBtn.setOnClickListener(view -> axeptio.clearConsents());
        sharedConsentsUrlBtn.setOnClickListener(view ->
                startActivity(new Intent(this, WebViewActivity.class))
        );
    }

    private void setGoogleConsent() {
        AxeptioSDK.instance().setEventListener(new AxeptioEventListener() {
            @Override
            public void onGoogleConsentModeUpdate(@NonNull Map<GoogleConsentType, ? extends GoogleConsentStatus> consentMap) {
                AxeptioEventListener.super.onGoogleConsentModeUpdate(consentMap);

                Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> firebaseConsentMap = new HashMap<>();

                for (Map.Entry<GoogleConsentType, ? extends GoogleConsentStatus> entry : consentMap.entrySet()) {
                    FirebaseAnalytics.ConsentType firebaseConsentType = null;
                    switch (entry.getKey()) {
                        case ANALYTICS_STORAGE:
                            firebaseConsentType = FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE;
                            break;
                        case AD_STORAGE:
                            firebaseConsentType = FirebaseAnalytics.ConsentType.AD_STORAGE;
                            break;
                        case AD_USER_DATA:
                            firebaseConsentType = FirebaseAnalytics.ConsentType.AD_USER_DATA;
                            break;
                        case AD_PERSONALIZATION:
                            firebaseConsentType = FirebaseAnalytics.ConsentType.AD_PERSONALIZATION;
                            break;
                    }

                    FirebaseAnalytics.ConsentStatus firebaseConsentStatus = null;
                    switch ((GoogleConsentStatus) entry.getValue()) {
                        case GRANTED:
                            firebaseConsentStatus = FirebaseAnalytics.ConsentStatus.GRANTED;
                            break;
                        case DENIED:
                            firebaseConsentStatus = FirebaseAnalytics.ConsentStatus.DENIED;
                            break;
                    }

                    if (firebaseConsentType != null && firebaseConsentStatus != null) {
                        firebaseConsentMap.put(firebaseConsentType, firebaseConsentStatus);
                    }

                }

                FirebaseAnalytics.getInstance(MainActivity.this).setConsent(firebaseConsentMap);
            }
        });
    }

    private void loadAd() {
        mViewModel.setAdLoading(true);

        InterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712",
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);

                        mViewModel.setAdLoading(false);
                        Toast.makeText(MainActivity.this, "Ad could not be loaded", Toast.LENGTH_SHORT).show();
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);

                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(MainActivity.this);
                        mViewModel.setAdLoading(false);
                    }
                });
    }

    private void showPreferencesDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.preferences_dialog, null);
        RecyclerView recycler = view.findViewById(R.id.recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new PreferencesAdapter(mViewModel.getSharedPreferences().toArray(new PrefencesItemUI[0])));

        new AlertDialog.Builder(MainActivity.this).setView(view).show();
    }

}
