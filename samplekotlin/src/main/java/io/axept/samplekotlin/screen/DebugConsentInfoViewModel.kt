package io.axept.samplekotlin.screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.axept.android.library.AxeptioSDK
import io.axept.android.library.AxeptioService
import io.axept.samplekotlin.BuildConfig
import io.axept.samplekotlin.config.ConfigurationManager
import java.text.SimpleDateFormat
import java.util.*

class DebugConsentInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val _debugInfo = MutableStateFlow(DebugInfoState())
    val debugInfo: StateFlow<DebugInfoState> = _debugInfo.asStateFlow()

    private val configurationManager = ConfigurationManager.getInstance(application)
    
    init {
        loadDebugInfo()
    }

    fun refreshDebugInfo() {
        loadDebugInfo()
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun loadDebugInfo() {
        try {
            val axeptioSDK = AxeptioSDK.instance()

            // Collect SDK information
            val sdkInfo = SdkInfo(
                version = BuildConfig.AXEPTIO_SDK_VERSION,
                clientId = BuildConfig.AXEPTIO_CLIENT_ID,
                cookiesVersion = BuildConfig.AXEPTIO_COOKIES_VERSION,
                targetService = BuildConfig.AXEPTIO_TARGET_SERVICE,
                isInitialized = axeptioSDK != null
            )

            // Collect configuration information
            val configInfo = ConfigInfo(
                currentService = configurationManager.currentConfiguration.targetService.name,
                serviceName = configurationManager.currentServiceDisplayName,
                availableConfigurations = listOf("Publishers TCF", "Brands")
            )

            // Collect consent information
            val consentInfo = try {
                val debugInfo = axeptioSDK.getConsentDebugInfo()
                val tcfString = debugInfo["IABTCF_TCString"] as? String ?: "No TCF string available"
                val hasConsent = debugInfo.isNotEmpty()

                ConsentInfo(
                    hasConsent = hasConsent,
                    consentString = tcfString,
                    lastUpdated = getCurrentTimestamp(),
                    rawDebugInfo = debugInfo
                )
            } catch (e: Exception) {
                ConsentInfo(
                    hasConsent = false,
                    consentString = "Error retrieving consent data: ${e.message}",
                    lastUpdated = getCurrentTimestamp(),
                    error = e.message
                )
            }

            // Collect vendor information (TCF specific)
            // Best Practice: These vendor APIs are only available for Publishers TCF service
            val vendorInfo = try {
                if (configurationManager.currentConfiguration.targetService == AxeptioService.PUBLISHERS_TCF) {
                    try {
                        // Best Practice: Always wrap vendor API calls in try-catch blocks
                        // These APIs may throw exceptions if SDK not initialized or no consent data
                        val vendorConsents = axeptioSDK.getVendorConsents()
                        val consentedVendors = axeptioSDK.getConsentedVendors()
                        val refusedVendors = axeptioSDK.getRefusedVendors()

                        // Calculate detailed breakdown
                        val totalCount = vendorConsents.size
                        val consentedCount = consentedVendors.size
                        val refusedCount = refusedVendors.size
                        val calculatedTotal = consentedCount + refusedCount

                        VendorInfo(
                            isAvailable = true,
                            totalVendors = totalCount.toString(),
                            consentedVendorsCount = consentedCount.toString(),
                            refusedVendorsCount = refusedCount.toString(),
                            calculatedTotal = calculatedTotal.toString(),
                            countDiscrepancy = if (totalCount != calculatedTotal) "⚠️ Count mismatch detected" else "",
                            vendorConsents = vendorConsents.mapKeys { it.key.toString() }.mapValues { it.value.toString() },
                            consentedVendorIds = consentedVendors,
                            refusedVendorIds = refusedVendors
                        )
                    } catch (apiException: Exception) {
                        VendorInfo(
                            isAvailable = true,
                            message = "Vendor API error: ${apiException.message}",
                            totalVendors = "Error",
                            consentedVendorsCount = "Error"
                        )
                    }
                } else {
                    VendorInfo(isAvailable = false, message = "Vendor API only available for TCF service")
                }
            } catch (e: Exception) {
                VendorInfo(isAvailable = false, message = "Error retrieving vendor info: ${e.message}")
            }

            // Collect technical details
            val technicalInfo = TechnicalInfo(
                timestamp = getCurrentTimestamp(),
                appVersion = "1.0", // From BuildConfig if available
                platform = "Android",
                osVersion = android.os.Build.VERSION.RELEASE,
                deviceModel = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
            )

            _debugInfo.value = DebugInfoState(
                isLoading = false,
                sdkInfo = sdkInfo,
                configInfo = configInfo,
                consentInfo = consentInfo,
                vendorInfo = vendorInfo,
                technicalInfo = technicalInfo,
                lastRefreshed = getCurrentTimestamp()
            )

        } catch (e: Exception) {
            _debugInfo.value = DebugInfoState(
                isLoading = false,
                error = "Failed to load debug information: ${e.message}"
            )
        }
    }

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    companion object {
        class Factory(private val application: Application) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return DebugConsentInfoViewModel(application) as T
            }
        }
    }
}

// Data classes for structured debug information
data class DebugInfoState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val sdkInfo: SdkInfo? = null,
    val configInfo: ConfigInfo? = null,
    val consentInfo: ConsentInfo? = null,
    val vendorInfo: VendorInfo? = null,
    val technicalInfo: TechnicalInfo? = null,
    val lastRefreshed: String = ""
)

data class SdkInfo(
    val version: String,
    val clientId: String,
    val cookiesVersion: String,
    val targetService: String,
    val isInitialized: Boolean
)

data class ConfigInfo(
    val currentService: String,
    val serviceName: String,
    val availableConfigurations: List<String>
)

data class ConsentInfo(
    val hasConsent: Boolean,
    val consentString: String,
    val lastUpdated: String,
    val error: String? = null,
    val rawDebugInfo: Map<String, Any?> = emptyMap()
)

data class VendorInfo(
    val isAvailable: Boolean,
    val message: String = "",
    val totalVendors: String = "0",
    val consentedVendorsCount: String = "0",
    val refusedVendorsCount: String = "0",
    val calculatedTotal: String = "0",
    val countDiscrepancy: String = "",
    val vendorConsents: Map<String, String> = emptyMap(),
    val consentedVendorIds: List<Int> = emptyList(),
    val refusedVendorIds: List<Int> = emptyList()
)

data class TechnicalInfo(
    val timestamp: String,
    val appVersion: String,
    val platform: String,
    val osVersion: String,
    val deviceModel: String
)