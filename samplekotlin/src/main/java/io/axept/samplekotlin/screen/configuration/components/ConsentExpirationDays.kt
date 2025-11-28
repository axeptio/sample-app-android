package io.axept.samplekotlin.screen.configuration.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.axept.samplekotlin.LocalAppTextFieldColors
import io.axept.samplekotlin.screen.configuration.ConfigurationUiState

@Composable
fun ConsentExpirationTextField(
    uiState: ConfigurationUiState,
    onConsentExpirationChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = uiState.consentExpirationDays,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                onConsentExpirationChange(newValue)
            }
        },
        label = { Text("Consent expiration (# of days)") },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        colors = LocalAppTextFieldColors.current
            ?: OutlinedTextFieldDefaults.colors()
    )
}
@Composable
fun ConsentExpirationSwitch(
    uiState: ConfigurationUiState,
    onConsentExpirationAcceptedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Do you want to override consent expiration days?",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (uiState.consentExpirationAccepted) "Yes" else "No",
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = uiState.consentExpirationAccepted,
                onCheckedChange = onConsentExpirationAcceptedChange
            )
        }
    }
}
