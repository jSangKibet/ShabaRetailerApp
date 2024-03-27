package com.acework.shabaretailer.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.R

@Composable
fun ButtonIconBlack(enabled: Boolean, iconResId: Int, onClick: () -> Unit) {
    Button(
        contentPadding = PaddingValues(16.dp),
        enabled = enabled,
        modifier = Modifier
            .height(48.dp)
            .width(48.dp),
        onClick = onClick,
        shape = RoundedCornerShape(0.dp)
    ) {
        Icon(
            contentDescription = stringResource(id = R.string.image_content_description),
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = iconResId),
            tint = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.inversePrimary
        )
    }
}