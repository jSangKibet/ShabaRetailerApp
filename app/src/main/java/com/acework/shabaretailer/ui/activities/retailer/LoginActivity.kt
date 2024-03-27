package com.acework.shabaretailer.ui.activities.retailer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.acework.shabaretailer.ui.components.buttons.ButtonBar
import com.acework.shabaretailer.ui.components.buttons.ButtonBarTranslucent
import com.acework.shabaretailer.ui.components.dialogs.DialogConfirmation
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.dialogs.DialogSuccess
import com.acework.shabaretailer.ui.components.fields.PasswordFieldTranslucent
import com.acework.shabaretailer.ui.components.fields.TextFieldTranslucent
import com.acework.shabaretailer.ui.components.snackbars.SnackbarModal
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.LoginViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Login(onLoginSuccess = { loginSuccess() }, toSignUp = { toSignUp() })
                }
            }
        }

    }

    private fun toSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun loginSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
fun Login(
    loginViewModel: LoginViewModel = viewModel(), onLoginSuccess: () -> Unit, toSignUp: () -> Unit
) {
    val loginUiState by loginViewModel.uiState.collectAsState()
    if (loginUiState.loginSuccess) {
        onLoginSuccess()
    }

    // parent container
    Box(modifier = Modifier.fillMaxSize()) {

        // content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.twende), contentScale = ContentScale.FillBounds
                )
        ) {
            // header
            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.image_content_description),
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp)
                )
                Text(
                    text = stringResource(id = R.string.welcome_to_shaba),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // content
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextFieldTranslucent(
                    errorMsg = R.string.invalid_input,
                    isError = loginUiState.emailError,
                    label = R.string.email_address,
                    onValueChange = { loginViewModel.updateEmail(it) },
                    value = loginUiState.email
                )
                PasswordFieldTranslucent(
                    errorMsg = R.string.field_required,
                    isError = loginUiState.passwordError,
                    label = R.string.password,
                    onValueChange = { loginViewModel.updatePassword(it) },
                    value = loginUiState.password
                )

                ButtonBar(
                    onClick = { loginViewModel.validate() },
                    text = stringResource(id = R.string.login)
                )
                ButtonBarTranslucent(onClick = toSignUp, stringResource(id = R.string.sign_up))
                ButtonBarTranslucent(
                    onClick = { loginViewModel.validateForgotPassword() },
                    text = stringResource(id = R.string.forgot_password)
                )

                // loading dialog
                if (loginUiState.loading) {
                    DialogLoading(text = stringResource(id = loginUiState.loadingMsg))
                }
            }
        }
    }

    // Snackbar
    if (loginUiState.loginError) {
        SnackbarModal(
            text = stringResource(id = R.string.login_failed),
            action = { loginViewModel.updateLoginError() },
            actionText = stringResource(
                id = R.string.okay_u
            )
        )
    }

    if (loginUiState.passwordReset == STATE_ERROR) {
        SnackbarModal(
            text = stringResource(id = R.string.password_reset_error),
            action = { loginViewModel.resetPasswordResetState() },
            actionText = stringResource(
                id = R.string.okay_u
            )
        )
    }

    if (loginUiState.forgotPassword) {
        DialogConfirmation(
            confirm = { loginViewModel.sendPasswordResetLink() },
            dismiss = { loginViewModel.setForgotPassword(false) },
            msg = stringResource(id = R.string.password_reset_prompt),
            title = stringResource(id = R.string.forgot_password)
        )
    }

    if (loginUiState.passwordReset == STATE_SUCCESS) {
        DialogSuccess(
            action = { loginViewModel.resetPasswordResetState() },
            text = stringResource(id = R.string.password_reset_success)
        )
    }
}