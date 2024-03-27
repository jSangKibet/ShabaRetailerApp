package com.acework.shabaretailer.ui.components.snackbars

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun SnackbarWithAction(buttonText: Int, onClick: () -> Unit, text: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = text)
                )
                TextButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = stringResource(id = buttonText).uppercase()
                    )
                }
            }
        }
    }
}