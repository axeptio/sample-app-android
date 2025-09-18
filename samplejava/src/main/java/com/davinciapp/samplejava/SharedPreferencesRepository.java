package com.davinciapp.samplejava;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

interface SharedPreferencesRepository {
    String get(String key, Class<?> type);
}

class SharedPreferencesRepositoryImpl implements SharedPreferencesRepository {

    Application application;
    SharedPreferences pref;

    SharedPreferencesRepositoryImpl(Application application) {
        this.application = application;
        this.pref = application.getSharedPreferences(
                application.getPackageName() + "_preferences",
                Context.MODE_PRIVATE
        );
    }

    @Override
    public String get(String key, Class<?> type) {
        if (type == String.class) {
            return pref.getString(key, "");
        } else if (type == Integer.class) {
            return Integer.toString(pref.getInt(key, 0));
        }
        return null;
    }
}

enum TCFFields {
    CmpSdkID("IABTCF_CmpSdkID", Integer.class),
    CmpSdkVersion("IABTCF_CmpSdkVersion", Integer.class),
    PolicyVersion("IABTCF_PolicyVersion", Integer.class),
    gdprApplies("IABTCF_gdprApplies", Integer.class),
    PublisherCC("IABTCF_PublisherCC", String.class),
    PurposeOneTreatment("IABTCF_PurposeOneTreatment", Integer.class),
    UseNonStandardTexts("IABTCF_UseNonStandardTexts", Integer.class),
    TCString("IABTCF_TCString", String.class),
    VendorConsents("IABTCF_VendorConsents", String.class),
    VendorLegitimateInterests("IABTCF_VendorLegitimateInterests", String.class),
    PurposeConsents("IABTCF_PurposeConsents", String.class),
    PurposeLegitimateInterests("IABTCF_PurposeLegitimateInterests", String.class),
    SpecialFeaturesOptIns("IABTCF_SpecialFeaturesOptIns", String.class),
    PublisherRestrictions("IABTCF_PublisherRestrictions", String.class),
    PublisherConsent("IABTCF_PublisherConsent", String.class),
    PublisherLegitimateInterests("IABTCF_PublisherLegitimateInterests", String.class),
    PublisherCustomPurposesConsents("IABTCF_PublisherCustomPurposesConsents", String.class),
    PublisherCustomPurposesLegitimateInterests(
            "IABTCF_PublisherCustomPurposesLegitimateInterests",
            String.class
    );

    String key;
    Class<?> type;

    private TCFFields(String key, Class<?> type) {
        this.key = key;
        this.type = type;
    }
}

enum COOKIE_FIELDS {
    AXEPTIO_COOKIES("axeptio_cookies"),
    AXEPTIO_ALL_VENDORS("axeptio_all_vendors"),
    AXEPTIO_AUTHORIZED_VENDORS("axeptio_authorized_vendors");

    private final String key;

    // Constructor
    COOKIE_FIELDS(String key) {
        this.key = key;
    }

    // Getter method to retrieve the key value
    public String getKey() {
        return key;
    }
}