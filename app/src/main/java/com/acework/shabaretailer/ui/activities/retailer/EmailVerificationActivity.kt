package com.acework.shabaretailer.ui.activities.retailer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.dialogs.DialogSuccess
import com.acework.shabaretailer.ui.components.snackbars.SnackbarModal
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutAction
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.EmailVerificationViewModel

class EmailVerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                ActivityRoot(
                    back = { finish() },
                    userVerified = { userVerified() }
                )
            }
        }
    }

    private fun userVerified() {
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
}

@Composable
private fun ActivityRoot(
    back: () -> Unit,
    userVerified: () -> Unit,
    viewModel: EmailVerificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            HeaderWithoutAction(
                back = back,
                subtitle = PostalService.retailer.email,
                title = stringResource(id = R.string.verify_email)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ButtonBar(
                    onClick = { viewModel.sendVerificationEmail() },
                    text = stringResource(id = R.string.send_verification_email)
                )
                ButtonBar(
                    onClick = { viewModel.reloadUser() },
                    text = stringResource(id = R.string.i_have_verified_my_email)
                )
            }
        }

        if (uiState.loading) {
            DialogLoading(text = stringResource(id = uiState.loadingMessage))
        }

        if (uiState.emailSent == STATE_SUCCESS) {
            DialogSuccess(
                action = { viewModel.emailSentAcknowledged() },
                text = stringResource(id = R.string.verification_email_sent)
            )
        }

        if (uiState.emailSent == STATE_ERROR) {
            SnackbarModal(
                text = stringResource(id = R.string.verification_email_not_sent),
                action = { viewModel.emailSentAcknowledged() },
                actionText = stringResource(id = R.string.okay_u)
            )
        }

        if (uiState.userReloaded == STATE_ERROR) {
            SnackbarModal(
                text = stringResource(id = R.string.error_checking_verification_status),
                action = { viewModel.userReloadedAcknowledged() },
                actionText = stringResource(id = R.string.okay_u)
            )
        }

        if (uiState.userVerified) {
            DialogSuccess(
                action = userVerified,
                text = stringResource(id = R.string.verification_success)
            )
        }
    }
}