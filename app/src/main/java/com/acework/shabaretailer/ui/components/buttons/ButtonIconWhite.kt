package com.acework.shabaretailer.ui.components.buttons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.acework.shabaretailer.R

@Composable
fun ButtonIconWhite(iconResId: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = stringResource(id = R.string.image_content_description),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}