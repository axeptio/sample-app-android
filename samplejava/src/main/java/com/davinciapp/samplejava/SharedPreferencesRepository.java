package com.davinciapp.samplejava;

import android.app.Application;
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
        this.pref = PreferenceManager.getDefaultSharedPreferences(application);
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
    PublisherRestrictions1("IABTCF_PublisherRestrictions1", String.class),
    PublisherRestrictions2("IABTCF_PublisherRestrictions2", String.class),
    PublisherRestrictions3("IABTCF_PublisherRestrictions3", String.class),
    PublisherRestrictions4("IABTCF_PublisherRestrictions4", String.class),
    PublisherRestrictions5("IABTCF_PublisherRestrictions5", String.class),
    PublisherRestrictions6("IABTCF_PublisherRestrictions6", String.class),
    PublisherRestrictions7("IABTCF_PublisherRestrictions7", String.class),
    PublisherRestrictions8("IABTCF_PublisherRestrictions8", String.class),
    PublisherRestrictions9("IABTCF_PublisherRestrictions9", String.class),
    PublisherRestrictions10("IABTCF_PublisherRestrictions10", String.class),
    PublisherRestrictions11("IABTCF_PublisherRestrictions11", String.class),
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