package com.acework.shabaretailer.ui.activities.retailer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.R
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import com.acework.shabaretailer.atlas.countriesWithCodes
import com.acework.shabaretailer.ui.activities.catalog.CatalogActivity
import com.acework.shabaretailer.ui.components.buttons.ButtonBar
import com.acework.shabaretailer.ui.components.buttons.ButtonBarOutlined
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.dialogs.DialogSuccess
import com.acework.shabaretailer.ui.components.fields.TextFieldTranslucent
import com.acework.shabaretailer.ui.components.snackbars.SnackbarWithAction
import com.acework.shabaretailer.ui.components.views.DropDownTranslucent
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutAction
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.EditRetailerViewModel

class EditRetailerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ActivityRoot(
                        back = { finish() },
                        onSuccess = { onSuccess() },
                        toChangePassword = {
                            startActivity(
                                Intent(this, ChangePasswordActivity::class.java)
                            )
                        },
                        toDeleteAccount = {
                            startActivity(
                                Intent(this, DeleteAccountActivity::class.java)
                            )
                        }
                    )
                }
            }
        }
    }

    private fun onSuccess() {
        val intent = Intent(this, CatalogActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

@Composable
private fun ActivityRoot(
    back: () -> Unit,
    viewModel: EditRetailerViewModel = viewModel(),
    onSuccess: () -> Unit,
    toChangePassword: () -> Unit,
    toDeleteAccount: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // header
            HeaderWithoutAction(
                back = back,
                subtitle = stringResource(id = R.string.provide_new_info),
                title = stringResource(id = R.string.edit_retailer)
            )

            /* INPUT FIELDS */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                //  name
                TextFieldTranslucent(
                    errorMsg = R.string.field_required,
                    isError = uiState.nameError,
                    label = R.string.your_business_name,
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


                // save changes
                ButtonBar(
                    onClick = { viewModel.validate() },
                    text = stringResource(id = R.string.save_changes)
                )

                Spacer(modifier = Modifier.weight(1f))

                ButtonBarOutlined(
                    onClick = toChangePassword,
                    text = stringResource(id = R.string.change_password)
                )

                ButtonBarOutlined(
                    onClick = toDeleteAccount,
                    text = stringResource(id = R.string.delete_account)
                )
            }
        }

        if (uiState.loading) {
            DialogLoading(text = stringResource(id = R.string.editing))
        }

        if (uiState.retailerEditingStatus == STATE_SUCCESS) {
            DialogSuccess(action = onSuccess, text = stringResource(id = R.string.edited))
        }

        if (uiState.retailerEditingStatus == STATE_ERROR) {
            SnackbarWithAction(
                buttonText = R.string.retry_u,
                onClick = { viewModel.validate() },
                text = R.string.error_editing
            )
        }
    }
}