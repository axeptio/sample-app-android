package io.axept.samplekotlin.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.axept.samplekotlin.ui.theme.SampleKotlinTheme
import io.axept.samplekotlin.ui.theme.YellowLight

@Composable
internal fun TokenInputPopup(
    onConfirm: (token: String) -> Unit,
    onDismiss: () -> Unit
) {

    var token by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = YellowLight
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter axeptio token",
                    style = MaterialTheme.typography.headlineMedium
                )
                TextField(
                    value = token,
                    onValueChange = { if (it.length < 50) token = it },
                    placeholder = {
                        Text(text = "Optional custom token")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                AxeptioButton(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = "Open in web view",
                    onClick = { onConfirm(token) }
                )
            }
        }

    }

}

@Preview
@Composable
private fun Preview() {
    SampleKotlinTheme {
        TokenInputPopup(onConfirm = {}, onDismiss = {})
    }
}