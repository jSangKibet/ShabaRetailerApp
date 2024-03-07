package com.acework.shabaretailer.ui.components.views.headers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.R
import com.acework.shabaretailer.ui.components.buttons.ButtonIconWhite

@Composable
fun HeaderWithoutAction(
    back: () -> Unit,
    subtitle: String,
    title: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // back button
        ButtonIconWhite(iconResId = R.drawable.bi_arrow_left_short, onClick = back)

        // text
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                text = title
            )
            Text(
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodySmall,
                text = subtitle
            )
        }
    }
}