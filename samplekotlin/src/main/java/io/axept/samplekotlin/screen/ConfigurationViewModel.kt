package io.axept.samplekotlin.screen

import androidx.lifecycle.ViewModel
import io.axept.android.library.AxeptioService
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
    val customTargetService: AxeptioService = AxeptioService.BRANDS,
    val forceShowConsent: Boolean = false,
    val displayPopUpOnEnterForeground: Boolean = true,
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
            customTargetService = currentConfig.targetService,
            forceShowConsent = currentConfig.forceShowConsent,
            displayPopUpOnEnterForeground = currentConfig.displayPopUpOnEnterForeground,
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
    
    fun updateTargetService(service: AxeptioService) {
        _uiState.value = _uiState.value.copy(
            customTargetService = service,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }
    
    fun updateForceShowConsent(force: Boolean) {
        _uiState.value = _uiState.value.copy(
            forceShowConsent = force,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }

    fun updateDisplayPopUpOnEnterForeground(display: Boolean) {
        _uiState.value = _uiState.value.copy(
            displayPopUpOnEnterForeground = display,
            selectedPreset = null,
            validationErrors = emptyList()
        )
    }

    fun saveCustomConfiguration(configManager: ConfigurationManager) {
        val currentState = _uiState.value
        // Carry over the widget-testing fields this screen does not expose, so saving a custom
        // configuration doesn't silently reset them to their defaults.
        val existing = configManager.currentConfiguration

        val customConfig = CustomerConfiguration(
            clientId = currentState.customClientId,
            cookiesVersion = currentState.customCookiesVersion,
            token = if (currentState.customToken.isBlank()) null else currentState.customToken,
            targetService = currentState.customTargetService,
            widgetType = existing.widgetType,
            prId = existing.prId,
            consentExpirationDays = existing.consentExpirationDays,
            shouldUpdateConsentExpiration = existing.shouldUpdateConsentExpiration,
            forceShowConsent = currentState.forceShowConsent,
            displayPopUpOnEnterForeground = currentState.displayPopUpOnEnterForeground,
        )
        
        val validationErrors = configManager.validateConfiguration(customConfig)
        
        if (validationErrors.isEmpty()) {
            configManager.currentConfiguration = customConfig
            // The popup toggles are runtime setters, so apply them now rather than waiting for the
            // next app start. clientId / cookiesVersion / targetService still need a restart.
            configManager.applyPopupSettingsToSdk()
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