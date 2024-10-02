package io.axept.samplekotlin.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.axept.samplekotlin.ui.theme.YellowLight

@Composable
fun PreferencesDisplay(state: MainViewModel.PreferenceStateUI) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).padding(vertical = 32.dp),
        shape = RoundedCornerShape(20.dp),
        color = YellowLight
    ) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp)
        ) {
            item {
                PreferencesTitle()
            }
            items(items = state.fields.toList()) { pair ->
                Column {
                    Text(
                        text = pair.first,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        text = pair.second,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PreferencesTitle(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp, top = 12.dp),
        text = "Shared Preferences",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    )
}