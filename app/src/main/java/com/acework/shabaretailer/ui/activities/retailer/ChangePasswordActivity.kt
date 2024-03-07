package com.acework.shabaretailer.ui.activities.retailer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.acework.shabaretailer.ui.activities.catalog.CatalogActivity
import com.acework.shabaretailer.ui.components.buttons.ButtonBar
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutAction
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.dialogs.DialogSuccess
import com.acework.shabaretailer.ui.components.fields.PasswordFieldTranslucent
import com.acework.shabaretailer.ui.components.snackbars.SnackbarWithAction
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.ChangePasswordViewModel

class ChangePasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChangePassword(back = { finish() }, onSuccess = { onSuccess() })
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
fun ChangePassword(
    back: () -> Unit,
    changePasswordViewModel: ChangePasswordViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    BackHandler { back() }
    val uiState by changePasswordViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // header
            HeaderWithoutAction(
                back = back,
                subtitle = stringResource(id = R.string.provide_new_info),
                title = stringResource(id =  R.string.change_password)
            )

            /* INPUT FIELDS */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f)
            ) {

                // current password
                PasswordFieldTranslucent(
                    errorMsg = R.string.wrong_password,
                    isError = uiState.currentPasswordError,
                    label = R.string.current_password,
                    onValueChange = { changePasswordViewModel.updateCurrentPassword(it) },
                    value = uiState.currentPassword
                )

                // new password
                PasswordFieldTranslucent(
                    errorMsg = R.string.password_short,
                    isError = uiState.newPasswordError,
                    label = R.string.new_password,
                    onValueChange = { changePasswordViewModel.updateNewPassword(it) },
                    value = uiState.newPassword
                )

                // confirm new password
                PasswordFieldTranslucent(
                    errorMsg = R.string.passwords_do_not_match,
                    isError = uiState.confirmNewPasswordError,
                    label = R.string.confirm_new_password,
                    onValueChange = { changePasswordViewModel.updateConfirmPassword(it) },
                    value = uiState.confirmNewPassword
                )

                // save changes
                ButtonBar(
                    onClick = { changePasswordViewModel.validate() },
                    text = stringResource(id = R.string.change_password)
                )
            }
        }

        if (uiState.errorChangingPassword) {
            SnackbarWithAction(
                buttonText = R.string.retry_u,
                onClick = { changePasswordViewModel.validate() },
                text = R.string.error_editing
            )
        }

        if (uiState.loading) {
            DialogLoading(text = stringResource(id = R.string.editing))
        }

        if (uiState.passwordChanged) {
            DialogSuccess(action = onSuccess, text = stringResource(id = R.string.edited))
        }
    }
}