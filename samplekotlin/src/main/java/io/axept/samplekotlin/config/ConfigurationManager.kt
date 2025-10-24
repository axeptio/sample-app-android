package io.axept.samplekotlin.config

import android.content.Context
import android.content.SharedPreferences
import io.axept.android.library.AxeptioService
import androidx.core.content.edit
import io.axept.android.library.WidgetType

data class CustomerConfiguration(
    val clientId: String,
    val cookiesVersion: String,
    val token: String?,
    val widgetType: WidgetType = WidgetType.PRODUCTION,
    val prId: String?,
    val targetService: AxeptioService
) {
    val displayName: String
        get() = "${if (targetService == AxeptioService.BRANDS) "Brands" else "TCF"}: $cookiesVersion"
}

class ConfigurationManager private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: ConfigurationManager? = null

        fun getInstance(context: Context): ConfigurationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ConfigurationManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "axeptio_config", Context.MODE_PRIVATE
    )

    // UserDefaults keys equivalent
    private object Keys {
        const val CLIENT_ID = "axeptio.config.clientId"
        const val COOKIES_VERSION = "axeptio.config.cookiesVersion"
        const val TOKEN = "axeptio.config.token"
        const val WIDGET_TYPE = "axeptio.config.widgetType"
        const val PR_ID = "axeptio.config.prId"
        const val TARGET_SERVICE = "axeptio.config.targetService"
        const val HAS_CUSTOM_CONFIGURATION = "axeptio.config.hasCustom"
    }

    // Default configurations for quick testing (matching iOS)
    val presetConfigurations: Map<String, CustomerConfiguration> = mapOf(
        "Default Brands" to CustomerConfiguration(
            clientId = "5fbfa806a0787d3985c6ee5f",
            cookiesVersion = "insideapp-brands",
            token = "5sj42u50ta2ys8c3nhjkxi",
            prId = "",
            targetService = AxeptioService.BRANDS
        ),
        "Default TCF" to CustomerConfiguration(
            clientId = "5fbfa806a0787d3985c6ee5f",
            cookiesVersion = "google cmp partner program sandbox-en-EU",
            token = "5sj42u50ta2ys8c3nhjkxi",
            prId = "",
            targetService = AxeptioService.PUBLISHERS_TCF
        ),
        "Test Brands (No Token)" to CustomerConfiguration(
            clientId = "5fbfa806a0787d3985c6ee5f",
            cookiesVersion = "insideapp-brands",
            token = null,
            prId = "",
            targetService = AxeptioService.BRANDS
        ),
        "Test TCF (No Token)" to CustomerConfiguration(
            clientId = "5fbfa806a0787d3985c6ee5f",
            cookiesVersion = "google cmp partner program sandbox-en-EU",
            token = null,
            prId = "",
            targetService = AxeptioService.PUBLISHERS_TCF
        )
    )

    // Current Configuration
    var currentConfiguration: CustomerConfiguration
        get() {
            val clientId = sharedPrefs.getString(Keys.CLIENT_ID, null) ?: "5fbfa806a0787d3985c6ee5f"
            val cookiesVersion =
                sharedPrefs.getString(Keys.COOKIES_VERSION, null) ?: "insideapp-brands"
            val token = sharedPrefs.getString(Keys.TOKEN, null)
            val widgetType = sharedPrefs.getInt(Keys.WIDGET_TYPE, 0)
            val prId = sharedPrefs.getString(Keys.PR_ID, null)
            val serviceOrdinal =
                sharedPrefs.getInt(Keys.TARGET_SERVICE, AxeptioService.BRANDS.ordinal)
            val targetService = AxeptioService.entries.toTypedArray()
                .getOrElse(serviceOrdinal) { AxeptioService.BRANDS }

            return CustomerConfiguration(
                clientId = clientId,
                cookiesVersion = cookiesVersion,
                token = token?.takeIf { it.isNotEmpty() },
                widgetType = WidgetType.entries[widgetType],
                prId = prId?.takeIf { it.isNotEmpty() },
                targetService = targetService
            )
        }
        set(value) {
            sharedPrefs.edit {
                putString(Keys.CLIENT_ID, value.clientId)
                    .putString(Keys.COOKIES_VERSION, value.cookiesVersion)
                    .putString(Keys.TOKEN, value.token)
                    .putInt(Keys.WIDGET_TYPE, value.widgetType.ordinal)
                    .putString(Keys.PR_ID, value.prId)
                    .putInt(Keys.TARGET_SERVICE, value.targetService.ordinal)
                    .putBoolean(Keys.HAS_CUSTOM_CONFIGURATION, true)
            }
        }

    val hasCustomConfiguration: Boolean
        get() = sharedPrefs.getBoolean(Keys.HAS_CUSTOM_CONFIGURATION, false)

    // Configuration Management
    fun loadPresetConfiguration(presetName: String) {
        presetConfigurations[presetName]?.let {
            currentConfiguration = it
        }
    }

    fun resetToDefault() {
        sharedPrefs.edit {
            remove(Keys.CLIENT_ID)
                .remove(Keys.COOKIES_VERSION)
                .remove(Keys.TOKEN)
                .remove(Keys.WIDGET_TYPE)
                .remove(Keys.PR_ID)
                .remove(Keys.TARGET_SERVICE)
                .remove(Keys.HAS_CUSTOM_CONFIGURATION)
        }
    }

    // Validation
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

        // Token validation is optional
        config.token?.let { token ->
            if (token.isNotEmpty() && token.length < 10) {
                errors.add("Token appears to be too short")
            }
        }

        return errors
    }

    // Display Helpers
    val currentServiceDisplayName: String
        get() = if (currentConfiguration.targetService == AxeptioService.BRANDS) "Brands" else "Publisher TCF"

    val currentServiceColor: String
        get() = if (currentConfiguration.targetService == AxeptioService.BRANDS) "AxeptioYellow" else "AxeptioBlueLight"

    // WebView URLs
    fun getWebViewURL(): String {
        return when (currentConfiguration.targetService) {
            AxeptioService.BRANDS -> "https://static.axept.io/app-sdk-webview-for-brands.html"
            AxeptioService.PUBLISHERS_TCF -> "https://google-cmp-partner.axept.io/cmp-for-publishers.html"
        }
    }
}