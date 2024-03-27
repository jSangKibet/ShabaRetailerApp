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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.ui.components.buttons.ButtonIconWhite

@Composable
fun Header(
    left: () -> Unit,
    leftIcon: Int,
    right: () -> Unit,
    rightIcon: Int,
    subtitle: Int,
    subtitleString: String = "",
    title: Int,
    titleString: String = ""
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // left
        ButtonIconWhite(iconResId = leftIcon, onClick = left)

        // text
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                text = titleString.ifEmpty { stringResource(id = title) }
            )
            Text(
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodySmall,
                text = subtitleString.ifEmpty { stringResource(id = subtitle) }
            )
        }

        // right
        ButtonIconWhite(iconResId = rightIcon, onClick = right)
    }
}