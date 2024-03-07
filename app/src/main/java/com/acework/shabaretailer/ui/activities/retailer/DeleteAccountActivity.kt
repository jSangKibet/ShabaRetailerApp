package com.acework.shabaretailer.ui.activities.retailer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.MainActivity
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.R
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import com.acework.shabaretailer.ui.components.buttons.ButtonBar
import com.acework.shabaretailer.ui.components.dialogs.DialogConfirmation
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.dialogs.DialogSuccess
import com.acework.shabaretailer.ui.components.fields.PasswordFieldTranslucent
import com.acework.shabaretailer.ui.components.snackbars.SnackbarModal
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutAction
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.DeleteAccountViewModel

class DeleteAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                ActivityRoot(
                    accountDeleted = {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    },
                    back = { finish() }
                )
            }
        }
    }
}

@Composable
private fun ActivityRoot(
    accountDeleted: () -> Unit,
    back: () -> Unit,
    viewModel: DeleteAccountViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderWithoutAction(
                back = back,
                subtitle = PostalService.retailer.name,
                title = stringResource(id = R.string.delete_account)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PasswordFieldTranslucent(
                    errorMsg = R.string.wrong_password,
                    isError = uiState.passwordError,
                    label = R.string.password,
                    onValueChange = { password -> viewModel.setPassword(password) },
                    value = uiState.password
                )
                ButtonBar(
                    onClick = { viewModel.setConfirmDeletion(true) },
                    text = stringResource(id = R.string.delete_account)
                )
            }
        }

        if (uiState.confirmingDeletion) {
            DialogConfirmation(
                confirm = { viewModel.deleteAccount() },
                dismiss = { viewModel.setConfirmDeletion(false) },
                msg = stringResource(id = R.string.confirm_account_deletion_msg),
                title = stringResource(id = R.string.confirm_account_deletion)
            )
        }

        if (uiState.loading) {
            DialogLoading(text = stringResource(id = R.string.loading))
        }

        if (uiState.accountDeleted == STATE_ERROR) {
            SnackbarModal(
                text = stringResource(id = R.string.account_deletion_error),
                action = { viewModel.consumeDeletionError() },
                actionText = stringResource(id = R.string.okay_u)
            )
        }

        if (uiState.accountDeleted == STATE_SUCCESS) {
            DialogSuccess(
                action = accountDeleted,
                text = stringResource(id = R.string.account_deleted)
            )
        }
    }
}