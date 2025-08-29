package io.axept.samplekotlin.screen

import androidx.lifecycle.ViewModel
import io.axept.android.library.AxeptioSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class VendorConsentTestUiState(
    val consentedVendors: List<Int> = emptyList(),
    val refusedVendors: List<Int> = emptyList(),
    val allVendorConsents: Map<Int, Boolean> = emptyMap(),
    val vendorIdText: String = "",
    val testResult: String? = null
)

class VendorConsentTestViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(VendorConsentTestUiState())
    val uiState: StateFlow<VendorConsentTestUiState> = _uiState.asStateFlow()
    
    init {
        refreshVendorData()
    }
    
    fun refreshVendorData() {
        try {
            val axeptio = AxeptioSDK.instance()
            
            // Best Practice: Check SDK initialization before calling vendor APIs
            // Get all vendor consent data using the new APIs
            val allVendorConsents = axeptio.getVendorConsents()
            val consentedVendors = axeptio.getConsentedVendors()
            val refusedVendors = axeptio.getRefusedVendors()
            
            _uiState.value = _uiState.value.copy(
                allVendorConsents = allVendorConsents,
                consentedVendors = consentedVendors,
                refusedVendors = refusedVendors
            )
        } catch (e: Exception) {
            // Handle potential SDK errors gracefully
            _uiState.value = _uiState.value.copy(
                allVendorConsents = emptyMap(),
                consentedVendors = emptyList(),
                refusedVendors = emptyList()
            )
        }
    }
    
    fun updateVendorId(vendorId: String) {
        _uiState.value = _uiState.value.copy(
            vendorIdText = vendorId,
            testResult = null // Clear previous result when typing
        )
    }
    
    fun testVendor() {
        val vendorIdText = _uiState.value.vendorIdText
        val vendorId = vendorIdText.toIntOrNull()
        
        if (vendorId == null || vendorId <= 0) {
            _uiState.value = _uiState.value.copy(
                testResult = "Invalid vendor ID. Please enter a positive number.",
                vendorIdText = "" // Clear the text field
            )
            return
        }
        
        try {
            val axeptio = AxeptioSDK.instance()
            // Best Practice: Use isVendorConsented() for compliance checks in data processing
            val isConsented = axeptio.isVendorConsented(vendorId)
            
            _uiState.value = _uiState.value.copy(
                testResult = "Vendor $vendorId: ${if (isConsented) "✅ CONSENTED" else "❌ REFUSED"}",
                vendorIdText = "" // Clear the text field
            )
        } catch (e: Exception) {
            // Best Practice: On error, assume no consent for safety
            _uiState.value = _uiState.value.copy(
                testResult = "Error testing vendor $vendorId: ${e.message}",
                vendorIdText = ""
            )
        }
    }
}