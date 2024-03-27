package com.acework.shabaretailer.ui.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.R

@Composable
fun ButtonIconTextWhite(
    icon: Painter,
    onClick: () -> Unit,
    text: String,
    iconLeft: Boolean = true
) {
    TextButton(onClick = onClick, shape = RoundedCornerShape(0.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconLeft) {
                Icon(
                    modifier = Modifier.size(16.dp, 16.dp),
                    painter = icon,
                    contentDescription = stringResource(id = R.string.image_content_description)
                )
            }
            Text(style = MaterialTheme.typography.titleSmall, text = text)
            if (!iconLeft) {
                Icon(
                    modifier = Modifier.size(16.dp, 16.dp),
                    painter = icon,
                    contentDescription = stringResource(id = R.string.image_content_description)
                )
            }
        }
    }
}