package io.axept.samplekotlin.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.axept.android.library.WidgetType
import io.axept.samplekotlin.config.ConfigurationManager
import io.axept.samplekotlin.config.CustomerConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    onBackClick: () -> Unit,
    viewModel: ConfigurationViewModel = viewModel()
) {
    val context = LocalContext.current
    val configManager = remember { ConfigurationManager.getInstance(context) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCurrentConfiguration(configManager)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuration") },
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
            // Current Configuration Display
            item {
                CurrentConfigurationSection(
                    currentConfig = configManager.currentConfiguration,
                    hasCustomConfig = configManager.hasCustomConfiguration
                )
            }

            // Preset Configurations
            item {
                Text(
                    text = "Preset Configurations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(configManager.presetConfigurations.toList()) { (name, config) ->
                PresetConfigurationCard(
                    name = name,
                    config = config,
                    isSelected = uiState.selectedPreset == name,
                    onSelect = {
                        viewModel.selectPreset(name)
                        configManager.loadPresetConfiguration(name)
                    }
                )
            }

            // Custom Configuration Section
            item {
                CustomConfigurationSection(
                    uiState = uiState,
                    onClientIdChange = viewModel::updateClientId,
                    onCookiesVersionChange = viewModel::updateCookiesVersion,
                    onTokenChange = viewModel::updateToken,
                    onWidgetTypeChange = viewModel::updateWidgetType,
                    onPrIdChange = viewModel::updatePrId,
                    onServiceChange = viewModel::updateTargetService,
                    onSaveCustomConfig = { viewModel.saveCustomConfiguration(configManager) },
                    onResetToDefault = {
                        configManager.resetToDefault()
                        viewModel.loadCurrentConfiguration(configManager)
                    }
                )
            }

            // Validation Errors
            if (uiState.validationErrors.isNotEmpty()) {
                item {
                    ValidationErrorsSection(errors = uiState.validationErrors)
                }
            }
        }
    }
}

@Composable
private fun CurrentConfigurationSection(
    currentConfig: CustomerConfiguration,
    hasCustomConfig: Boolean
) {
    Column {
        Text(
            text = "Current Configuration",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConfigurationDetailRow("Service", currentConfig.targetService.name)
                ConfigurationDetailRow("Client ID", currentConfig.clientId)
                ConfigurationDetailRow("Cookies Version", currentConfig.cookiesVersion)
                ConfigurationDetailRow("Token", currentConfig.token ?: "None")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Type:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (hasCustomConfig) "Custom" else "Default",
                        color = if (hasCustomConfig) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigurationDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun PresetConfigurationCard(
    name: String,
    config: CustomerConfiguration,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = config.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CustomConfigurationSection(
    uiState: ConfigurationUiState,
    onClientIdChange: (String) -> Unit,
    onCookiesVersionChange: (String) -> Unit,
    onTokenChange: (String) -> Unit,
    onWidgetTypeChange: (WidgetType) -> Unit,
    onPrIdChange: (String) -> Unit,
    onServiceChange: (io.axept.android.library.AxeptioService) -> Unit,
    onSaveCustomConfig: () -> Unit,
    onResetToDefault: () -> Unit
) {
    Column {
        Text(
            text = "Custom Configuration",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.customClientId,
                    onValueChange = onClientIdChange,
                    label = { Text("Client ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.customCookiesVersion,
                    onValueChange = onCookiesVersionChange,
                    label = { Text("Cookies Version") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.customToken,
                    onValueChange = onTokenChange,
                    label = { Text("Token (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                WidgetTypeDropdown(uiState, onWidgetTypeChange)

                if (uiState.widgetType != WidgetType.PRODUCTION) {
                    OutlinedTextField(
                        value = uiState.prId,
                        onValueChange = onPrIdChange,
                        label = { Text("PR ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Service Selection
                Column(modifier = Modifier.selectableGroup()) {
                    Text(
                        text = "Target Service:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (uiState.customTargetService == io.axept.android.library.AxeptioService.BRANDS),
                                onClick = { onServiceChange(io.axept.android.library.AxeptioService.BRANDS) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (uiState.customTargetService == io.axept.android.library.AxeptioService.BRANDS),
                            onClick = null
                        )
                        Text(
                            text = "Brands",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (uiState.customTargetService == io.axept.android.library.AxeptioService.PUBLISHERS_TCF),
                                onClick = { onServiceChange(io.axept.android.library.AxeptioService.PUBLISHERS_TCF) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (uiState.customTargetService == io.axept.android.library.AxeptioService.PUBLISHERS_TCF),
                            onClick = null
                        )
                        Text(
                            text = "Publishers TCF",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetToDefault,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset to Default")
                    }

                    Button(
                        onClick = onSaveCustomConfig,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Custom")
                    }
                }
            }
        }
    }
}

@Composable
private fun ValidationErrorsSection(errors: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Validation Errors:",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold
            )

            errors.forEach { error ->
                Text(
                    text = "• $error",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetTypeDropdown(
    uiState: ConfigurationUiState,
    onWidgetTypeChange: (WidgetType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = uiState.widgetType.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Widget type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WidgetType.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        expanded = false
                        onWidgetTypeChange(option)
                    }
                )
            }
        }
    }
}
