package com.acework.shabaretailer.ui.components.dialogs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.R

@Composable
fun DialogConfirmation(confirm: () -> Unit, dismiss: () -> Unit, msg: String, title: String) {
    AlertDialog(
        confirmButton = {
            TextButton(
                onClick = confirm,
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = stringResource(id = R.string.yes)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        dismissButton = {
            TextButton(
                onClick = dismiss,
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = stringResource(id = R.string.no)
                )
            }
        },
        onDismissRequest = dismiss,
        shape = RoundedCornerShape(0.dp),
        title = {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = title
            )
        },
        text = {
            Text(
                style = MaterialTheme.typography.titleSmall,
                text = msg
            )
        }
    )
}