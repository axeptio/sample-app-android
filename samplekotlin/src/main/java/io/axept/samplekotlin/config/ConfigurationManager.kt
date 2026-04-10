package io.axept.samplekotlin.config

import android.content.Context
import android.content.SharedPreferences
import io.axept.android.library.AxeptioService
import io.axept.android.library.WidgetType

data class CustomerConfiguration(
    val clientId: String,
    val cookiesVersion: String,
    val token: String?,
    val targetService: AxeptioService,
    val widgetType: WidgetType = WidgetType.PRODUCTION,
    val prId: String? = null,
    val consentExpirationDays: Int = 190,
    val shouldUpdateConsentExpiration: Boolean = false,
    val forceShowConsent: Boolean = false,
) {
    val displayName: String
        get() = "${if (targetService == AxeptioService.BRANDS) "Brands" else "TCF"}: $cookiesVersion"
}

class ConfigurationManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: ConfigurationManager? = null

        fun getInstance(context: Context): ConfigurationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ConfigurationManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        private const val DEFAULT_CLIENT_ID = "5fbfa806a0787d3985c6ee5f"
        private const val DEFAULT_COOKIES_VERSION = "google cmp partner program sandbox-en-EU"
        private const val DEFAULT_CONSENT_EXPIRATION_DAYS = 190
    }

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "axeptio_config", Context.MODE_PRIVATE
    )

    private object Keys {
        const val CLIENT_ID = "axeptio.config.clientId"
        const val COOKIES_VERSION = "axeptio.config.cookiesVersion"
        const val TOKEN = "axeptio.config.token"
        const val TARGET_SERVICE = "axeptio.config.targetService"
        const val HAS_CUSTOM_CONFIGURATION = "axeptio.config.hasCustom"
        const val WIDGET_TYPE = "axeptio.config.widgetType"
        const val PR_ID = "axeptio.config.prId"
        const val CONSENT_EXPIRATION_DAYS = "axeptio.config.consentExpirationDays"
        const val SHOULD_UPDATE_CONSENT_EXPIRATION = "axeptio.config.shouldUpdateConsentExpiration"
        const val FORCE_SHOW_CONSENT = "axeptio.config.forceShowConsent"
    }

    val presetConfigurations: Map<String, CustomerConfiguration> = mapOf(
        "Default Brands" to CustomerConfiguration(
            clientId = DEFAULT_CLIENT_ID,
            cookiesVersion = DEFAULT_COOKIES_VERSION,
            token = "5sj42u50ta2ys8c3nhjkxi",
            targetService = AxeptioService.BRANDS
        ),
        "Default TCF" to CustomerConfiguration(
            clientId = DEFAULT_CLIENT_ID,
            cookiesVersion = DEFAULT_COOKIES_VERSION,
            token = "5sj42u50ta2ys8c3nhjkxi",
            targetService = AxeptioService.PUBLISHERS_TCF
        ),
        "Test Brands (No Token)" to CustomerConfiguration(
            clientId = DEFAULT_CLIENT_ID,
            cookiesVersion = DEFAULT_COOKIES_VERSION,
            token = null,
            targetService = AxeptioService.BRANDS
        ),
        "Test TCF (No Token)" to CustomerConfiguration(
            clientId = DEFAULT_CLIENT_ID,
            cookiesVersion = DEFAULT_COOKIES_VERSION,
            token = null,
            targetService = AxeptioService.PUBLISHERS_TCF
        )
    )

    var currentConfiguration: CustomerConfiguration
        get() {
            val clientId = sharedPrefs.getString(Keys.CLIENT_ID, null) ?: DEFAULT_CLIENT_ID
            val cookiesVersion = sharedPrefs.getString(Keys.COOKIES_VERSION, null) ?: DEFAULT_COOKIES_VERSION
            val token = sharedPrefs.getString(Keys.TOKEN, null)
            val serviceOrdinal = sharedPrefs.getInt(Keys.TARGET_SERVICE, AxeptioService.BRANDS.ordinal)
            val targetService = AxeptioService.values().getOrElse(serviceOrdinal) { AxeptioService.BRANDS }
            val widgetTypeOrdinal = sharedPrefs.getInt(Keys.WIDGET_TYPE, WidgetType.PRODUCTION.ordinal)
            val widgetType = WidgetType.values().getOrElse(widgetTypeOrdinal) { WidgetType.PRODUCTION }
            val prId = sharedPrefs.getString(Keys.PR_ID, null)
            val consentExpirationDays = sharedPrefs.getInt(
                Keys.CONSENT_EXPIRATION_DAYS, DEFAULT_CONSENT_EXPIRATION_DAYS
            )
            val shouldUpdateConsentExpiration = sharedPrefs.getBoolean(
                Keys.SHOULD_UPDATE_CONSENT_EXPIRATION, false
            )
            val forceShowConsent = sharedPrefs.getBoolean(Keys.FORCE_SHOW_CONSENT, false)

            return CustomerConfiguration(
                clientId = clientId,
                cookiesVersion = cookiesVersion,
                token = if (token.isNullOrEmpty()) null else token,
                targetService = targetService,
                widgetType = widgetType,
                prId = if (prId.isNullOrEmpty()) null else prId,
                consentExpirationDays = consentExpirationDays,
                shouldUpdateConsentExpiration = shouldUpdateConsentExpiration,
                forceShowConsent = forceShowConsent,
            )
        }
        set(value) {
            sharedPrefs.edit()
                .putString(Keys.CLIENT_ID, value.clientId)
                .putString(Keys.COOKIES_VERSION, value.cookiesVersion)
                .putString(Keys.TOKEN, value.token)
                .putInt(Keys.TARGET_SERVICE, value.targetService.ordinal)
                .putInt(Keys.WIDGET_TYPE, value.widgetType.ordinal)
                .putString(Keys.PR_ID, value.prId)
                .putInt(Keys.CONSENT_EXPIRATION_DAYS, value.consentExpirationDays)
                .putBoolean(Keys.SHOULD_UPDATE_CONSENT_EXPIRATION, value.shouldUpdateConsentExpiration)
                .putBoolean(Keys.FORCE_SHOW_CONSENT, value.forceShowConsent)
                .putBoolean(Keys.HAS_CUSTOM_CONFIGURATION, true)
                .apply()
        }

    val hasCustomConfiguration: Boolean
        get() = sharedPrefs.getBoolean(Keys.HAS_CUSTOM_CONFIGURATION, false)

    fun loadPresetConfiguration(presetName: String) {
        presetConfigurations[presetName]?.let {
            currentConfiguration = it
        }
    }

    fun resetToDefault() {
        sharedPrefs.edit()
            .remove(Keys.CLIENT_ID)
            .remove(Keys.COOKIES_VERSION)
            .remove(Keys.TOKEN)
            .remove(Keys.TARGET_SERVICE)
            .remove(Keys.WIDGET_TYPE)
            .remove(Keys.PR_ID)
            .remove(Keys.CONSENT_EXPIRATION_DAYS)
            .remove(Keys.SHOULD_UPDATE_CONSENT_EXPIRATION)
            .remove(Keys.FORCE_SHOW_CONSENT)
            .remove(Keys.HAS_CUSTOM_CONFIGURATION)
            .apply()
    }

    fun validateConfiguration(config: CustomerConfiguration): List<String> {
        val errors = mutableListOf<String>()

        if (config.clientId.isEmpty()) {
            errors.add("Client ID is required")
        } else if (config.clientId.length < 10) {
            errors.add("Client ID appears to be too short")
        }

        if (config.cookiesVersion.isEmpty()) {
            errors.add("Cookies Version is required")
        }

        config.token?.let { token ->
            if (token.isNotEmpty() && token.length < 10) {
                errors.add("Token appears to be too short")
            }
        }

        if (config.consentExpirationDays <= 0) {
            errors.add("Consent expiration must be a positive number of days")
        }

        return errors
    }

    val currentServiceDisplayName: String
        get() = if (currentConfiguration.targetService == AxeptioService.BRANDS) "Brands" else "Publisher TCF"

    val currentServiceColor: String
        get() = if (currentConfiguration.targetService == AxeptioService.BRANDS) "AxeptioYellow" else "AxeptioBlueLight"

    fun getWebViewURL(): String {
        return when (currentConfiguration.targetService) {
            AxeptioService.BRANDS -> "https://static.axept.io/app-sdk-webview-for-brands.html"
            AxeptioService.PUBLISHERS_TCF -> "https://google-cmp-partner.axept.io/cmp-for-publishers.html"
        }
    }
}
