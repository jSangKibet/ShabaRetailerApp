package com.acework.shabaretailer.ui.activities.retailer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.MainActivity
import com.acework.shabaretailer.R
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import com.acework.shabaretailer.atlas.countriesWithCodes
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.dialogs.DialogSuccess
import com.acework.shabaretailer.ui.components.fields.TextFieldTranslucent
import com.acework.shabaretailer.ui.components.snackbars.SnackbarWithAction
import com.acework.shabaretailer.ui.components.views.DropDownTranslucent
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutAction
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.CreateRetailerViewModel


class CreateRetailerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ActivityRoot(
                        back = { onBackPressedDispatcher.onBackPressed() }
                    )
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            back()
        }
    }

    private fun back() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
private fun ActivityRoot(
    back: () -> Unit,
    viewModel: CreateRetailerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(id = R.drawable.twende), contentScale = ContentScale.FillHeight)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // header
            HeaderWithoutAction(
                back = back,
                subtitle = stringResource(id = R.string.finish_creating),
                title = stringResource(id = R.string.hello_retailer)
            )

            /* INPUT FIELDS */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f)
            ) {

                //  name
                TextFieldTranslucent(
                    errorMsg = R.string.field_required,
                    isError = uiState.nameError,
                    label = R.string.your_shops_name,
                    onValueChange = { name ->
                        viewModel.updateFields(name = name)
                    },
                    value = uiState.name
                )

                // country
                DropDownTranslucent(
                    errorMsg = stringResource(id = R.string.invalid_input),
                    isError = uiState.countryError,
                    dropdownItems = countriesWithCodes.keys.toList(),
                    label = stringResource(id = R.string.country),
                    onValueChange = { country ->
                        viewModel.updateFields(country = country)
                    },
                    value = uiState.country
                )

                //  city
                TextFieldTranslucent(
                    errorMsg = R.string.field_required,
                    isError = uiState.cityError,
                    label = R.string.city,
                    onValueChange = { city ->
                        viewModel.updateFields(city = city)
                    },
                    value = uiState.city
                )

                // finish button
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.validate() },
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.finish_u).uppercase(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        if (uiState.loading) {
            DialogLoading(text = stringResource(id = R.string.creating))
        }

        if (uiState.retailerCreationStatus == STATE_SUCCESS) {
            DialogSuccess(action = back, text = stringResource(id = R.string.created))
        }

        if (uiState.retailerCreationStatus == STATE_ERROR) {
            SnackbarWithAction(
                buttonText = R.string.retry_u,
                onClick = { viewModel.validate() },
                text = R.string.error_creating
            )
        }
    }
}