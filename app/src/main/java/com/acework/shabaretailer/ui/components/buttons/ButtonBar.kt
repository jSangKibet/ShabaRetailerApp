package com.acework.shabaretailer.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonBar(onClick: () -> Unit, text: String) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onClick,
        shape = RoundedCornerShape(0.dp)
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = text
        )
    }
}