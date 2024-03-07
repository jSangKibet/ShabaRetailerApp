package com.acework.shabaretailer.ui.components.views.headers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.ui.components.buttons.ButtonIconWhite

@Composable
fun HeaderWithoutSubtitle(
    left: () -> Unit,
    leftIcon: Int,
    right: () -> Unit,
    rightIcon: Int,
    title: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // back button
        ButtonIconWhite(iconResId = leftIcon, onClick = left)

        // text
        Text(
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            text = title
        )

        // action button
        ButtonIconWhite(iconResId = rightIcon, onClick = right)
    }
}