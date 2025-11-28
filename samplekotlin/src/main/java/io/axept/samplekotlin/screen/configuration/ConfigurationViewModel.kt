package io.axept.samplekotlin.screen.configuration

import androidx.lifecycle.ViewModel
import io.axept.android.library.AxeptioService
import io.axept.android.library.WidgetType
import io.axept.samplekotlin.config.CONSENT_EXPIRATION_DAYS
import io.axept.samplekotlin.config.ConfigurationManager
import io.axept.samplekotlin.config.CustomerConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ConfigurationUiState(
    val selectedPreset: String? = null,
    val customClientId: String = "",
    val customCookiesVersion: String = "",
    val customToken: String = "",
    val widgetType: WidgetType = WidgetType.PRODUCTION,
    val prId: String = "",
    val consentExpirationDays: String = CONSENT_EXPIRATION_DAYS,
    val consentExpirationAccepted: Boolean = false,
    val customTargetService: AxeptioService = AxeptioService.BRANDS,
    val validationErrors: List<String> = emptyList()
)

class ConfigurationViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ConfigurationUiState())
    val uiState: StateFlow<ConfigurationUiState> = _uiState.asStateFlow()
    
    fun loadCurrentConfiguration(configManager: ConfigurationManager) {
        val currentConfig = configManager.currentConfiguration
        
        _uiState.value = _uiState.value.copy(
            customClientId = currentConfig.clientId,
            customCookiesVersion = currentConfig.cookiesVersion,
            customToken = currentConfig.token ?: "",
            widgetType = currentConfig.widgetType,
            prId = currentConfig.prId ?: "",
            consentExpirationDays = currentConfig.consentExpirationDays,
            consentExpirationAccepted = false,
            customTargetService = currentConfig.targetService,
            validationErrors = emptyList(),
            selectedPreset = findMatchingPreset(currentConfig, configManager)
        )
    }
    
    private fun findMatchingPreset(config: CustomerConfiguration, configManager: ConfigurationManager): String? {
        return configManager.presetConfigurations.entries.find { (_, presetConfig) ->
            presetConfig.clientId == config.clientId &&
            presetConfig.cookiesVersion == config.cookiesVersion &&
            presetConfig.token == config.token &&
            presetConfig.targetService == config.targetService
        }?.key
    }
    
    fun selectPreset(presetName: String) {
        _uiState.value = _uiState.value.copy(
            selectedPreset = presetName,
            validationErrors = emptyList()
        )
    }
    
    fun updateClientId(clientId: String) {
        _uiState.value = _uiState.value.copy(
            customClientId = clientId,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }
    
    fun updateCookiesVersion(cookiesVersion: String) {
        _uiState.value = _uiState.value.copy(
            customCookiesVersion = cookiesVersion,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }
    
    fun updateToken(token: String) {
        _uiState.value = _uiState.value.copy(
            customToken = token,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }

    fun updateWidgetType(widgetType: WidgetType) {
        _uiState.value = _uiState.value.copy(
            widgetType = widgetType,
            prId = if(widgetType != WidgetType.PR) "" else _uiState.value.prId,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }

    fun updatePrId(prId: String) {
        _uiState.value = _uiState.value.copy(
            prId = prId,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }

    fun updateConsentExpiration(expirationDays: String) {
        _uiState.value = _uiState.value.copy(
            consentExpirationDays = expirationDays,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }

    fun updateConsentExpirationAccepted(expirationAccepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            consentExpirationAccepted = expirationAccepted,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }
    
    fun updateTargetService(service: AxeptioService) {
        _uiState.value = _uiState.value.copy(
            customTargetService = service,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }
    
    fun saveCustomConfiguration(configManager: ConfigurationManager) {
        val currentState = _uiState.value
        
        val customConfig = CustomerConfiguration(
            clientId = currentState.customClientId,
            cookiesVersion = currentState.customCookiesVersion,
            token = currentState.customToken.ifBlank { null },
            widgetType = currentState.widgetType,
            prId = currentState.prId.ifBlank { null },
            targetService = currentState.customTargetService,
            consentExpirationDays = currentState.consentExpirationDays,
            consentExpirationAccepted = currentState.consentExpirationAccepted,
        )
        
        val validationErrors = configManager.validateConfiguration(customConfig)
        
        if (validationErrors.isEmpty()) {
            configManager.currentConfiguration = customConfig
            _uiState.value = currentState.copy(
                validationErrors = emptyList(),
                selectedPreset = null // It's now a custom configuration
            )
        } else {
            _uiState.value = currentState.copy(
                validationErrors = validationErrors
            )
        }
    }
}