package com.acework.shabaretailer.ui.components.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.R

@Composable
fun DropDownTranslucent(
    errorMsg: String,
    isError: Boolean,
    dropdownItems: List<String>,
    label: String,
    onValueChange: (String) -> Unit,
    value: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {

        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                errorContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = !expanded
                            }
                        }
                    }
                },
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text(style = MaterialTheme.typography.titleSmall, text = label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = true
                },
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(0.dp),
            supportingText = {
                if (isError) {
                    Text(text = errorMsg)
                }
            },
            textStyle = MaterialTheme.typography.titleSmall,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.bi_caret_down_fill),
                        contentDescription = stringResource(id = R.string.image_content_description)
                    )
                }
            },
            value = value
        )

        DropdownMenu(
            expanded = expanded,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            onDismissRequest = { expanded = false }) {
            Box (modifier = Modifier.height(600.dp).width(240.dp)){
                LazyColumn {
                    items(dropdownItems) { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    style = MaterialTheme.typography.titleSmall,
                                    text = item
                                )
                            },
                            onClick = {
                                onValueChange(item)
                                expanded = false
                            })
                    }
                }
            }
        }
    }
}