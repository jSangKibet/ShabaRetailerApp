package com.acework.shabaretailer.ui.components.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldTranslucent(
    errorMsg: Int,
    isError: Boolean,
    label: Int,
    onValueChange: (String) -> Unit,
    value: String
) {
    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(
            errorContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        isError = isError,
        label = {
            Text(
                style = MaterialTheme.typography.titleSmall,
                text = stringResource(id = label)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        onValueChange = onValueChange,
        shape = RoundedCornerShape(0.dp),
        supportingText = {
            if (isError) {
                Text(text = stringResource(id = errorMsg))
            }
        },
        textStyle = MaterialTheme.typography.titleSmall,
        value = value
    )
}