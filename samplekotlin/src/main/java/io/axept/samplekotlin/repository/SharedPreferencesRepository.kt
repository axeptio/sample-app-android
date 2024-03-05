package io.axept.samplekotlin.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

internal interface SharedPreferencesRepository {
    fun store(key: String, value: Any, type: Any)
    fun get(key: String, type: Any): String

    val hasUpdated: Flow<String?>
    fun clearListener()
    fun listen()
}

internal class SharedPreferencesRepositoryImpl(appContext: Context) : SharedPreferencesRepository {

    private var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)


    private val _hasUpdated = MutableStateFlow<String?>(null)
    override val hasUpdated: StateFlow<String?> = _hasUpdated

    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun clearListener() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        listener = null
    }

    override fun listen() {
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            _hasUpdated.update { key }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun store(key: String, value: Any, type: Any) {
        when (type) {
            String -> sharedPreferences.edit().putString(key, (value as String)).apply()
            Int -> sharedPreferences.edit().putInt(key, (value as Int)).apply()
        }
    }

    override fun get(key: String, type: Any): String {
        return when (type) {
            String -> sharedPreferences.getString(key, "") ?: ""
            Int -> sharedPreferences.getInt(key, 0).toString()
            else -> ""
        }
    }

}

enum class TCFFields(val key: String, val type: Any) {
    CmpSdkID("IABTCF_CmpSdkID", Int),
    CmpSdkVersion("IABTCF_CmpSdkVersion", Int),
    PolicyVersion("IABTCF_PolicyVersion", Int),
    gdprApplies("IABTCF_gdprApplies", Int),
    PublisherCC("IABTCF_PublisherCC", String),
    PurposeOneTreatment("IABTCF_PurposeOneTreatment", Int),
    UseNonStandardTexts("IABTCF_UseNonStandardTexts", Int),
    TCString("IABTCF_TCString", String),
    VendorConsents("IABTCF_VendorConsents", String),
    VendorLegitimateInterests("IABTCF_VendorLegitimateInterests", String),
    PurposeConsents("IABTCF_PurposeConsents", String),
    PurposeLegitimateInterests("IABTCF_PurposeLegitimateInterests", String),
    SpecialFeaturesOptIns("IABTCF_SpecialFeaturesOptIns", String),
    PublisherRestrictions1("IABTCF_PublisherRestrictions1", String),
    PublisherRestrictions2("IABTCF_PublisherRestrictions2", String),
    PublisherRestrictions3("IABTCF_PublisherRestrictions3", String),
    PublisherRestrictions4("IABTCF_PublisherRestrictions4", String),
    PublisherRestrictions5("IABTCF_PublisherRestrictions5", String),
    PublisherRestrictions6("IABTCF_PublisherRestrictions6", String),
    PublisherRestrictions7("IABTCF_PublisherRestrictions7", String),
    PublisherRestrictions8("IABTCF_PublisherRestrictions8", String),
    PublisherRestrictions9("IABTCF_PublisherRestrictions9", String),
    PublisherRestrictions10("IABTCF_PublisherRestrictions10", String),
    PublisherRestrictions11("IABTCF_PublisherRestrictions11", String),
    PublisherConsent("IABTCF_PublisherConsent", String),
    PublisherLegitimateInterests("IABTCF_PublisherLegitimateInterests", String),
    PublisherCustomPurposesConsents("IABTCF_PublisherCustomPurposesConsents", String),
    PublisherCustomPurposesLegitimateInterests(
        "IABTCF_PublisherCustomPurposesLegitimateInterests",
        String
    );
}
