package io.axept.samplekotlin.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugConsentInfoScreen(
    onNavigateBack: () -> Unit,
    viewModel: DebugConsentInfoViewModel = viewModel(
        factory = DebugConsentInfoViewModel.Companion.Factory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val debugInfo by viewModel.debugInfo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Debug Consent Info") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshDebugInfo() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                debugInfo.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                debugInfo.error != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "Error: ${debugInfo.error}",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Last refreshed info
                        item {
                            Text(
                                text = "Last refreshed: ${debugInfo.lastRefreshed}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Consent State Verification Section
                        item {
                            ExpandableDebugSection(
                                title = "🧹 Consent State Verification",
                                initiallyExpanded = true
                            ) {
                                debugInfo.consentInfo?.let { consentInfo ->
                                    val hasAnyConsent = consentInfo.hasConsent ||
                                            consentInfo.rawDebugInfo.isNotEmpty() ||
                                            consentInfo.consentString.isNotBlank()

                                    if (!hasAnyConsent) {
                                        DebugInfoItem("✅ State", "Clean - No consent data found")
                                        DebugInfoItem(
                                            "Ready for Testing",
                                            "You can now show consent popup for testing"
                                        )
                                    } else {
                                        DebugInfoItem(
                                            "⚠️ State",
                                            "Consent data exists - not clean",
                                            isError = true
                                        )
                                        DebugInfoItem(
                                            "Action Needed",
                                            "Use 'Clear consent' button to reset",
                                            isError = true
                                        )
                                    }

                                    // Show quick state summary
                                    debugInfo.vendorInfo?.let { vendorInfo ->
                                        DebugInfoItem(
                                            "Vendor Count",
                                            "${vendorInfo.totalVendors} total, ${vendorInfo.consentedVendorsCount} consented"
                                        )
                                    }

                                    val rawDataCount = consentInfo.rawDebugInfo.size
                                    DebugInfoItem(
                                        "Raw Debug Fields",
                                        "$rawDataCount IABTCF fields present"
                                    )

                                    if (consentInfo.consentString.isNotBlank() && consentInfo.consentString != "No TCF string available") {
                                        DebugInfoItem(
                                            "TCF String Status",
                                            "Present (${consentInfo.consentString.length} chars)"
                                        )
                                    } else {
                                        DebugInfoItem("TCF String Status", "Empty/Not found")
                                    }
                                }

                                // Clear Consent Instructions
                                Text(
                                    text = "🔄 Testing Workflow",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                DebugInfoItem("1. Clear", "Tap 'Clear consent' on main screen")
                                DebugInfoItem(
                                    "2. Refresh",
                                    "Tap refresh button above to verify clean state"
                                )
                                DebugInfoItem(
                                    "3. Test",
                                    "Show consent popup and try 'Accept All' vs manual selection"
                                )
                                DebugInfoItem(
                                    "4. Compare",
                                    "Use this debug screen to compare results"
                                )
                            }
                        }

                        // SDK Information Section
                        debugInfo.sdkInfo?.let { sdkInfo ->
                            item {
                                ExpandableDebugSection(
                                    title = "SDK Information",
                                    initiallyExpanded = true
                                ) {
                                    DebugInfoItem("Version", sdkInfo.version)
                                    DebugInfoItem("Client ID", sdkInfo.clientId)
                                    DebugInfoItem("Cookies Version", sdkInfo.cookiesVersion)
                                    DebugInfoItem("Target Service", sdkInfo.targetService)
                                    DebugInfoItem(
                                        "Is Initialized",
                                        if (sdkInfo.isInitialized) "✅ Yes" else "❌ No"
                                    )
                                }
                            }
                        }

                        // Configuration Information Section
                        debugInfo.configInfo?.let { configInfo ->
                            item {
                                ExpandableDebugSection(
                                    title = "Configuration",
                                    initiallyExpanded = true
                                ) {
                                    DebugInfoItem("Current Service", configInfo.currentService)
                                    DebugInfoItem("Service Name", configInfo.serviceName)
                                    DebugInfoItem(
                                        "Available Configs",
                                        configInfo.availableConfigurations.joinToString(", ")
                                    )
                                }
                            }
                        }

                        // Consent Information Section
                        debugInfo.consentInfo?.let { consentInfo ->
                            item {
                                ExpandableDebugSection(
                                    title = "Consent Information",
                                    initiallyExpanded = true
                                ) {
                                    DebugInfoItem(
                                        "Has Consent",
                                        if (consentInfo.hasConsent) "✅ Yes" else "❌ No"
                                    )
                                    DebugInfoItem("Last Updated", consentInfo.lastUpdated)

                                    if (consentInfo.error != null) {
                                        DebugInfoItem("Error", consentInfo.error, isError = true)
                                    }

                                    CopyableDebugInfoItem(
                                        "TCF Consent String",
                                        consentInfo.consentString
                                    )
                                }
                            }
                        }

                        // TCF Bit String Analysis Section
                        debugInfo.consentInfo?.rawDebugInfo?.let { rawInfo ->
                            val vendorConsentsString = rawInfo["IABTCF_VendorConsents"] as? String
                            val vendorLegitimateInterestsString =
                                rawInfo["IABTCF_VendorLegitimateInterests"] as? String

                            if (!vendorConsentsString.isNullOrEmpty() || !vendorLegitimateInterestsString.isNullOrEmpty()) {
                                item {
                                    ExpandableDebugSection(
                                        title = "TCF Bit String Analysis",
                                        initiallyExpanded = true
                                    ) {
                                        Text(
                                            text = "🔍 Understanding TCF Vendor Consent Data",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )

                                        DebugInfoItem(
                                            "Explanation",
                                            "Each character represents a vendor ID (position 0 = vendor 1)"
                                        )
                                        DebugInfoItem(
                                            "'1' = Consented",
                                            "'0' = Refused or Not Available"
                                        )

                                        // Vendor Consents Analysis
                                        vendorConsentsString?.let { consentString ->
                                            Text(
                                                text = "📊 IABTCF_VendorConsents Analysis",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )

                                            val totalLength = consentString.length
                                            val consentedCount = consentString.count { it == '1' }
                                            val refusedCount = consentString.count { it == '0' }
                                            val invalidCount =
                                                totalLength - consentedCount - refusedCount

                                            DebugInfoItem(
                                                "String Length",
                                                "$totalLength characters"
                                            )
                                            DebugInfoItem(
                                                "Consented ('1')",
                                                "$consentedCount vendors"
                                            )
                                            DebugInfoItem("Refused ('0')", "$refusedCount vendors")
                                            if (invalidCount > 0) {
                                                DebugInfoItem(
                                                    "Invalid chars",
                                                    "$invalidCount characters",
                                                    isError = true
                                                )
                                            }

                                            val consentPercentage = if (totalLength > 0) {
                                                String.format(
                                                    Locale.getDefault(),
                                                    "%.2f%%",
                                                    (consentedCount.toDouble() / totalLength) * 100,
                                                )
                                            } else "0%"
                                            DebugInfoItem(
                                                "Consent Rate",
                                                "$consentPercentage of possible vendors"
                                            )

                                            // Show pattern of first/last 20 characters
                                            if (consentString.length >= 20) {
                                                val firstChars = consentString.take(20)
                                                val lastChars = consentString.takeLast(20)
                                                DebugInfoItem(
                                                    "First 20 chars",
                                                    "\"$firstChars\" (vendors 1-20)"
                                                )
                                                if (consentString.length > 20) {
                                                    DebugInfoItem(
                                                        "Last 20 chars",
                                                        "\"$lastChars\" (vendors ${totalLength - 19}-$totalLength)"
                                                    )
                                                }
                                            }

                                            CopyableDebugInfoItem(
                                                "Full VendorConsents String",
                                                consentString
                                            )
                                        }

                                        // Vendor Legitimate Interests Analysis
                                        vendorLegitimateInterestsString?.let { legitString ->
                                            Text(
                                                text = "⚖️ IABTCF_VendorLegitimateInterests Analysis",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )

                                            val totalLength = legitString.length
                                            val acceptedCount = legitString.count { it == '1' }
                                            val refusedCount = legitString.count { it == '0' }

                                            DebugInfoItem(
                                                "String Length",
                                                "$totalLength characters"
                                            )
                                            DebugInfoItem(
                                                "Accepted ('1')",
                                                "$acceptedCount vendors"
                                            )
                                            DebugInfoItem("Refused ('0')", "$refusedCount vendors")

                                            val legitPercentage = if (totalLength > 0) {
                                                String.format(
                                                    Locale.getDefault(),
                                                    "%.2f%%",
                                                    (acceptedCount.toDouble() / totalLength) * 100
                                                )
                                            } else "0%"
                                            DebugInfoItem(
                                                "Legitimate Interest Rate",
                                                "$legitPercentage of possible vendors"
                                            )

                                            CopyableDebugInfoItem(
                                                "Full LegitimateInterests String",
                                                legitString
                                            )
                                        }

                                        // Combined Analysis
                                        if (!vendorConsentsString.isNullOrEmpty() && !vendorLegitimateInterestsString.isNullOrEmpty()) {
                                            Text(
                                                text = "🔗 Combined Analysis",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )

                                            val consentCount =
                                                vendorConsentsString.count { it == '1' }
                                            val legitCount =
                                                vendorLegitimateInterestsString.count { it == '1' }
                                            val totalAccepted = consentCount + legitCount

                                            DebugInfoItem(
                                                "Consent + Legit Interest",
                                                "$totalAccepted total vendor approvals"
                                            )
                                            DebugInfoItem(
                                                "Breakdown",
                                                "$consentCount consent + $legitCount legitimate interest"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Raw Debug Information Section - Show all IABTCF_ fields
                        debugInfo.consentInfo?.rawDebugInfo?.takeIf { it.isNotEmpty() }
                            ?.let { rawInfo ->
                                item {
                                    ExpandableDebugSection(
                                        title = "All Raw Debug Fields",
                                        initiallyExpanded = false
                                    ) {
                                        // Sort the keys to show IABTCF_ fields first
                                        val sortedEntries =
                                            rawInfo.entries.sortedWith(compareBy { entry ->
                                                when {
                                                    entry.key.startsWith("IABTCF_") -> "0_${entry.key}"
                                                    else -> "1_${entry.key}"
                                                }
                                            })

                                        sortedEntries.forEach { (key, value) ->
                                            val displayValue = when (value) {
                                                is String -> if (value.length > 100) {
                                                    "${value.take(50)}...${value.takeLast(20)} (${value.length} chars)"
                                                } else value

                                                null -> "null"
                                                else -> value.toString()
                                            }

                                            if (value is String && (key.contains("TCString") || key.contains(
                                                    "VendorConsents"
                                                ) || value.length > 50)
                                            ) {
                                                CopyableDebugInfoItem(key, value)
                                            } else {
                                                DebugInfoItem(key, displayValue)
                                            }
                                        }
                                    }
                                }
                            }

                        // Vendor Information Section
                        debugInfo.vendorInfo?.let { vendorInfo ->
                            item {
                                ExpandableDebugSection(
                                    title = "Vendor Information",
                                    initiallyExpanded = false
                                ) {
                                    if (vendorInfo.isAvailable) {
                                        // Count Analysis Section
                                        Text(
                                            text = "📊 Vendor Count Analysis",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )

                                        DebugInfoItem(
                                            "Total Vendors (Map)",
                                            vendorInfo.totalVendors
                                        )
                                        DebugInfoItem(
                                            "Consented Vendors",
                                            vendorInfo.consentedVendorsCount
                                        )
                                        DebugInfoItem(
                                            "Refused Vendors",
                                            vendorInfo.refusedVendorsCount
                                        )
                                        DebugInfoItem(
                                            "Calculated Total",
                                            "${vendorInfo.calculatedTotal} (${vendorInfo.consentedVendorsCount} + ${vendorInfo.refusedVendorsCount})"
                                        )

                                        if (vendorInfo.countDiscrepancy.isNotBlank()) {
                                            DebugInfoItem(
                                                "⚠️ Discrepancy",
                                                vendorInfo.countDiscrepancy,
                                                isError = true
                                            )
                                            val missing = vendorInfo.totalVendors.toIntOrNull()
                                                ?.let { total ->
                                                    vendorInfo.calculatedTotal.toIntOrNull()
                                                        ?.let { calculated ->
                                                            total - calculated
                                                        }
                                                }
                                            if (missing != null && missing != 0) {
                                                DebugInfoItem(
                                                    "Missing Count",
                                                    "$missing vendors unaccounted for",
                                                    isError = true
                                                )
                                            }
                                        } else {
                                            DebugInfoItem(
                                                "✅ Validation",
                                                "All vendors accounted for"
                                            )
                                        }

                                        if (!vendorInfo.message.isBlank()) {
                                            DebugInfoItem("Status", vendorInfo.message)
                                        }

                                        // Vendor ID Lists Section
                                        if (vendorInfo.consentedVendorIds.isNotEmpty() || vendorInfo.refusedVendorIds.isNotEmpty()) {
                                            Text(
                                                text = "🆔 Vendor ID Lists",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )

                                            if (vendorInfo.consentedVendorIds.isNotEmpty()) {
                                                CopyableDebugInfoItem(
                                                    "Consented IDs (${vendorInfo.consentedVendorIds.size})",
                                                    vendorInfo.consentedVendorIds.sorted()
                                                        .joinToString(", ")
                                                )
                                            }

                                            if (vendorInfo.refusedVendorIds.isNotEmpty()) {
                                                CopyableDebugInfoItem(
                                                    "Refused IDs (${vendorInfo.refusedVendorIds.size})",
                                                    vendorInfo.refusedVendorIds.sorted()
                                                        .joinToString(", ")
                                                )
                                            }
                                        }

                                        // Individual Vendor Consents (Sample)
                                        if (vendorInfo.vendorConsents.isNotEmpty()) {
                                            Text(
                                                text = "🔍 Individual Vendor Consents (First 10)",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )

                                            // Show first 10 vendors sorted by ID
                                            vendorInfo.vendorConsents.entries
                                                .sortedBy { it.key.toIntOrNull() ?: Int.MAX_VALUE }
                                                .take(10)
                                                .forEach { (vendorId, consent) ->
                                                    DebugInfoItem(
                                                        "Vendor $vendorId",
                                                        if (consent == "true") "✅ Consented" else "❌ Refused"
                                                    )
                                                }

                                            if (vendorInfo.vendorConsents.size > 10) {
                                                DebugInfoItem(
                                                    "...",
                                                    "And ${vendorInfo.vendorConsents.size - 10} more vendors"
                                                )
                                            }
                                        } else if (vendorInfo.totalVendors == "0") {
                                            DebugInfoItem(
                                                "Note",
                                                "No vendor data available yet. Show consent popup first."
                                            )
                                        } else if (vendorInfo.totalVendors != "Error") {
                                            DebugInfoItem(
                                                "Vendor Details",
                                                "Vendor consent data is being processed..."
                                            )
                                        }
                                    } else {
                                        DebugInfoItem("Status", vendorInfo.message)
                                        if (vendorInfo.message.contains("only available for TCF service")) {
                                            DebugInfoItem(
                                                "Note",
                                                "Switch to Publishers service to access vendor APIs"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Technical Information Section
                        debugInfo.technicalInfo?.let { technicalInfo ->
                            item {
                                ExpandableDebugSection(
                                    title = "Technical Information",
                                    initiallyExpanded = false
                                ) {
                                    DebugInfoItem("Platform", technicalInfo.platform)
                                    DebugInfoItem("OS Version", technicalInfo.osVersion)
                                    DebugInfoItem("Device Model", technicalInfo.deviceModel)
                                    DebugInfoItem("App Version", technicalInfo.appVersion)
                                    DebugInfoItem("Debug Timestamp", technicalInfo.timestamp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableDebugSection(
    title: String,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun DebugInfoItem(
    label: String,
    value: String,
    isError: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f),
            color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
        )
    }
}

@Composable
private fun CopyableDebugInfoItem(
    label: String,
    value: String
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(value))
                    // Show a brief feedback (you might want to add a Snackbar here)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Copy to clipboard",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}