package com.acework.shabaretailer.ui.components.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.R

@Composable
fun PasswordFieldTranslucent(
    errorMsg: Int,
    isError: Boolean,
    label: Int,
    onValueChange: (String) -> Unit,
    value: String
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(
            errorContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon =
                    if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                Icon(
                    imageVector = visibilityIcon,
                    contentDescription = stringResource(id = R.string.image_content_description)
                )
            }
        },
        value = value,
        visualTransformation =
        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
    )
}