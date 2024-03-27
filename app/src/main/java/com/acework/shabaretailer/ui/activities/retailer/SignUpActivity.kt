package com.acework.shabaretailer.ui.activities.retailer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.MainActivity
import com.acework.shabaretailer.R
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.fields.PasswordFieldTranslucent
import com.acework.shabaretailer.ui.components.fields.TextFieldTranslucent
import com.acework.shabaretailer.ui.components.snackbars.SnackbarModal
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutAction
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.SignUpStates
import com.acework.shabaretailer.ui.viewmodel.SignUpViewModel


class SignUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignUp(back = { finish() }, onSuccess = { onSuccess() }, openTC = { openTC() })
                }
            }
        }
    }

    private fun onSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    private fun openTC() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theshaba.com/terms-of-use"))
        startActivity(browserIntent)
    }
}

@Composable
fun SignUp(
    back: () -> Unit,
    onSuccess: () -> Unit,
    openTC: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel()
) {
    val signUpUiState by signUpViewModel.uiState.collectAsState()

    // overall container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(id = R.drawable.twende), contentScale = ContentScale.FillHeight)
    ) {

        // main content
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderWithoutAction(
                back = back,
                subtitle = stringResource(id = R.string.hello_retailer),
                title = stringResource(id = R.string.provide_your_details_to_join_shaba)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                TextFieldTranslucent(
                    errorMsg = R.string.invalid_input,
                    isError = signUpUiState.signUpFieldErrors.emailError,
                    label = R.string.email_address,
                    onValueChange = { signUpViewModel.updateEmail(it) },
                    value = signUpUiState.payload.email
                )

                PasswordFieldTranslucent(
                    errorMsg = R.string.field_required,
                    isError = signUpUiState.signUpFieldErrors.passwordError,
                    label = R.string.password,
                    onValueChange = { signUpViewModel.updatePassword(it) },
                    value = signUpUiState.payload.password
                )

                PasswordFieldTranslucent(
                    errorMsg = R.string.passwords_do_not_match,
                    isError = signUpUiState.signUpFieldErrors.confirmPasswordError,
                    label = R.string.confirm_password,
                    onValueChange = { signUpViewModel.updateConfirmPassword(it) },
                    value = signUpUiState.payload.confirmPassword
                )


                TcField(
                    isError = signUpUiState.signUpFieldErrors.tcAcceptedError,
                    onChange = { signUpViewModel.updateTCAccepted(it) },
                    openTC = openTC,
                    value = signUpUiState.payload.tcAccepted
                )

                ButtonBlack(textResId = R.string.join) { signUpViewModel.validateInput() }
            }
        }

        // dialogs
        if (signUpUiState.currentState == SignUpStates.SIGNING_UP) {
            DialogLoading(text = stringResource(id = R.string.signing_you_up))
        }

        if (signUpUiState.currentState == SignUpStates.SIGN_UP_SUCCEEDED) {
            SnackbarModal(
                text = stringResource(id = R.string.sign_up_successful),
                action = onSuccess,
                actionText = stringResource(
                    id = R.string.okay
                )
            )
        }

        if (signUpUiState.currentState == SignUpStates.SIGNUP_FAILED) {
            SnackbarModal(
                text = stringResource(id = R.string.error_signing_up),
                action = { signUpViewModel.signUpFailed() },
                actionText = stringResource(
                    id = R.string.ok
                )
            )
        }
    }
}

@Composable
private fun TcField(
    isError: Boolean,
    onChange: (value: Boolean) -> Unit,
    openTC: () -> Unit,
    value: Boolean,
) {

    val modifier = Modifier
        .background(color = Color(0x99FFFFFF), shape = RoundedCornerShape(4.dp))
        .fillMaxWidth()
        .padding(4.dp)

    Column(
        modifier = if (isError) {
            modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(4.dp)
            )
        } else {
            modifier
        },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row {
            Checkbox(
                checked = value,
                onCheckedChange = { onChange(it) })
            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                text = stringResource(id = R.string.ra_the_tc)
            )
        }
        Spacer(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .height(1.dp)
        )
        TextButton(onClick = openTC, shape = RoundedCornerShape(0.dp)) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                text = stringResource(id = R.string.click_here_to_read_tc).uppercase(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ButtonBlack(textResId: Int, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(0.dp)
    ) {
        Text(
            text = stringResource(id = textResId).uppercase(),
            style = MaterialTheme.typography.titleMedium
        )
    }
}