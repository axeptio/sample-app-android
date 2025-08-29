package io.axept.samplekotlin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorConsentTestScreen(
    onBackClick: () -> Unit,
    viewModel: VendorConsentTestViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Auto-refresh every 10 seconds (reduced frequency for production example)
    // Note: In production apps, consider user-triggered refresh instead of auto-refresh
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000) // 10 seconds - more reasonable for battery/performance
            viewModel.refreshVendorData()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TCF Vendor API") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Summary Section
            item {
                SummarySection(
                    consentedCount = uiState.consentedVendors.size,
                    refusedCount = uiState.refusedVendors.size,
                    totalCount = uiState.allVendorConsents.size
                )
            }
            
            // Test Specific Vendor Section
            item {
                TestVendorSection(
                    vendorIdText = uiState.vendorIdText,
                    testResult = uiState.testResult,
                    onVendorIdChange = viewModel::updateVendorId,
                    onTestVendor = {
                        viewModel.testVendor()
                        keyboardController?.hide()
                    }
                )
            }
            
            // Consented Vendors List
            item {
                VendorListSection(
                    title = "Consented Vendors (getConsentedVendors)",
                    vendors = uiState.consentedVendors,
                    emptyMessage = "No vendors consented"
                )
            }
            
            // Refused Vendors List  
            item {
                VendorListSection(
                    title = "Refused Vendors (getRefusedVendors)",
                    vendors = uiState.refusedVendors,
                    emptyMessage = "No vendors refused"
                )
            }
            
            // All Vendors Consents
            item {
                AllVendorsSection(
                    title = "All Vendor Consents (getVendorConsents)",
                    vendorConsents = uiState.allVendorConsents
                )
            }
        }
    }
}

@Composable
private fun SummarySection(
    consentedCount: Int,
    refusedCount: Int,
    totalCount: Int
) {
    Column {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryCard(
                    title = "Consented",
                    count = consentedCount,
                    color = Color(0xFF4CAF50)
                )
                
                SummaryCard(
                    title = "Refused", 
                    count = refusedCount,
                    color = Color(0xFFF44336)
                )
                
                SummaryCard(
                    title = "Total",
                    count = totalCount,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TestVendorSection(
    vendorIdText: String,
    testResult: String?,
    onVendorIdChange: (String) -> Unit,
    onTestVendor: () -> Unit
) {
    Column {
        Text(
            text = "Test Specific Vendor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = vendorIdText,
                        onValueChange = onVendorIdChange,
                        label = { Text("Vendor ID") },
                        placeholder = { Text("e.g., 123") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Button(
                        onClick = onTestVendor,
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text("Test Vendor")
                    }
                }
                
                testResult?.let { result ->
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = when {
                            result.contains("CONSENTED") -> Color(0xFF4CAF50)
                            result.contains("REFUSED") -> Color(0xFFF44336)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                } ?: run {
                    Text(
                        text = "Enter a vendor ID to test",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun VendorListSection(
    title: String,
    vendors: List<Int>,
    emptyMessage: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(16.dp)
            ) {
                if (vendors.isEmpty()) {
                    Text(
                        text = emptyMessage,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Text(
                        text = formatVendorList(vendors),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun AllVendorsSection(
    title: String,
    vendorConsents: Map<Int, Boolean>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                if (vendorConsents.isEmpty()) {
                    Text(
                        text = "No vendor consent data available",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Text(
                        text = formatAllVendorsData(vendorConsents),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }
    }
}

private fun formatVendorList(vendors: List<Int>): String {
    val maxVendorsToShow = 50
    val sortedVendors = vendors.sorted()
    
    return if (sortedVendors.size <= maxVendorsToShow) {
        sortedVendors.joinToString(", ")
    } else {
        val visibleVendors = sortedVendors.take(maxVendorsToShow)
        val remaining = sortedVendors.size - maxVendorsToShow
        visibleVendors.joinToString(", ") + "\n\n... and $remaining more"
    }
}

private fun formatAllVendorsData(vendorConsents: Map<Int, Boolean>): String {
    val sortedVendors = vendorConsents.toList().sortedBy { it.first }
    val maxVendorsToShow = 30
    
    val result = StringBuilder()
    
    sortedVendors.take(maxVendorsToShow).forEach { (vendorId, isConsented) ->
        val status = if (isConsented) "✅" else "❌"
        result.append("$vendorId: $status\n")
    }
    
    if (vendorConsents.size > maxVendorsToShow) {
        val remaining = vendorConsents.size - maxVendorsToShow
        result.append("\n... and $remaining more vendors")
    }
    
    return result.toString()
}